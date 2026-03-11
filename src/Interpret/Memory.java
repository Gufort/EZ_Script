package Interpret;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Memory {
    private static final int STATIC_SIZE = 32768;
    private static final int DYNAMIC_SIZE = 32768;

    private static final ByteBuffer staticMemory = ByteBuffer.allocate(STATIC_SIZE);
    private static final ByteBuffer dynamicMemory = ByteBuffer.allocate(DYNAMIC_SIZE);

    private static int staticNextAddress = 0;
    private static int dynamicNextAddress = 0;

    // => Отслеживание выделения памяти
    private static Map<Integer, StaticAllocation> staticAllocations = new HashMap<>();
    private static Map<Integer, DynamicAllocation> dynamicAllocations = new HashMap<>();

    private static AllocationStrategy strategy = AllocationStrategy.FIRST_FIT;


    // => Свободные блоки
    private static List<FreeBlock> freeBlocks = new ArrayList<>();

    static{
        staticMemory.order(ByteOrder.BIG_ENDIAN);
        dynamicMemory.order(ByteOrder.BIG_ENDIAN);
        freeBlocks.add(new FreeBlock(0, DYNAMIC_SIZE));
    }

    // => Вспомогательные внутренние классы
    public enum DataType{
        INT(4),
        DOUBLE(8),
        BOOLEAN(1),
        BIG_INTEGER(-1),
        ARRAY_POINTER(4);

        public final int fixedSize;
        DataType(int size) { this.fixedSize = size; }
        public boolean hasFixedSize() { return fixedSize > 0; }
    }

    public enum AllocationStrategy{
        FIRST_FIT, // в первое попавшееся свободное место
        BEST_FIT,  // в самое маленькое подходящее свободное место
        WORST_FIT  // в самое большое подходящее свободное место
    }

    private static class StaticAllocation{
        int address;
        int size;
        DataType type;
        StaticAllocation(int address, int size, DataType type) {
            this.address = address;
            this.size = size;
            this.type = type;
        }
    }

    private static class DynamicAllocation{
        int address;
        int size;
        DataType type;
        DynamicAllocation(int address, int size, DataType type) {
            this.address = address;
            this.size = size;
            this.type = type;
        }
    }

    private static class FreeBlock{
        int address;
        int size;
        FreeBlock(int address, int size){
            this.address = address;
            this.size = size;
        }
    }

    // Метод для задания стратегии выбора свободных блоков
    public static void setAllocationStrategy(AllocationStrategy s) {
        strategy = s;
    }


    // => Выделения статической памяти
    private static int allocateStatic(int size){
        if(staticNextAddress + size > STATIC_SIZE)
            throw new RuntimeException("Out of static memory!");
        int address = staticNextAddress;
        staticNextAddress += size;
        return address;
    }

    public static int allocateInt(int value){
        int address = allocateStatic(DataType.INT.fixedSize);
        staticMemory.putInt(address, value);
        staticAllocations.put(address, new StaticAllocation(address, DataType.INT.fixedSize, DataType.INT));
        return address;
    }

    public static int allocateDouble(double value) {
        int addr = allocateStatic(DataType.DOUBLE.fixedSize);
        staticMemory.putDouble(addr, value);
        staticAllocations.put(addr, new StaticAllocation(addr, DataType.DOUBLE.fixedSize, DataType.DOUBLE));
        return addr;
    }

    public static int allocateBoolean(boolean value) {
        int addr = allocateStatic(DataType.BOOLEAN.fixedSize);
        staticMemory.put(addr, (byte)(value ? 1 : 0));
        staticAllocations.put(addr, new StaticAllocation(addr, DataType.BOOLEAN.fixedSize, DataType.BOOLEAN));
        return addr;
    }

    public static int allocateBigInteger(BigInteger value){
        byte[] bytes = value.toByteArray();
        int dynamicAddress = allocateDynamic(bytes.length);
        dynamicMemory.position(dynamicAddress);
        dynamicMemory.put(bytes);
        int ptrAddress = allocateStatic(DataType.ARRAY_POINTER.fixedSize);
        staticMemory.putInt(ptrAddress, dynamicAddress);
        dynamicAllocations.put(dynamicAddress, new DynamicAllocation(dynamicAddress, bytes.length, DataType.BIG_INTEGER));
        return ptrAddress;
    }


    // => Выделения динамической памяти
    private static int allocateDynamic(int size){
        FreeBlock selectedBlock = null;
        int selectedIndex = -1;

        switch (strategy){
            case FIRST_FIT:
                for (int i = 0; i < freeBlocks.size(); i++) {
                    FreeBlock block = freeBlocks.get(i);
                    if (block.size >= size) {
                        selectedBlock = block;
                        selectedIndex = i;
                        break;
                    }
                }
                break;
            case BEST_FIT:
                for (int i = 0; i < freeBlocks.size(); i++) {
                    FreeBlock block = freeBlocks.get(i);
                    if (block.size >= size) {
                        if (selectedBlock == null || block.size < selectedBlock.size) {
                            selectedBlock = block;
                            selectedIndex = i;
                        }
                    }
                }
                break;
            case WORST_FIT:
                for(int i = 0; i < freeBlocks.size(); i++){
                    FreeBlock block = freeBlocks.get(i);
                    if(block.size >= size){
                        if(selectedBlock == null || block.size > selectedBlock.size){
                            selectedBlock = block;
                            selectedIndex = i;
                        }
                    }
                }
                break;
        }

        if(selectedBlock == null)
            throw new RuntimeException("Out of memory exception: динамическая память заполнена!");


        int address = selectedBlock.address;
        if (selectedBlock.size == size) {
            freeBlocks.remove(selectedIndex);
        } else {
            selectedBlock.address += size;
            selectedBlock.size -= size;
        } // откусываем кусок от блока и передвигаем адрес его начала
        return address;
    }

    private static void freeDynamic(int address, int size){
        freeBlocks.add(new FreeBlock(address, size));
        coalesceFreeBlocks();
    }

    private static void coalesceFreeBlocks(){
        freeBlocks.sort(Comparator.comparingInt(a -> a.address));
        for(int i = 0; i < freeBlocks.size() - 1; i++){
            var current = freeBlocks.get(i);
            var next = freeBlocks.get(i + 1);
            if(current.address + current.size == next.address){
                current.size += next.size;
                freeBlocks.remove(i + 1);
                i--;
            }
        }
    }


    // => Массивы
    public static int allocateArray(DataType elementType, int size){
        if(elementType.hasFixedSize()){
            int totalSize = elementType.fixedSize * size;
            int dynamicAddress = allocateDynamic(totalSize);
            // Инициализация нулями
            for (int i = 0; i < totalSize; i++) {
                dynamicMemory.put(dynamicAddress + i, (byte) 0);
            }
            int ptrAddress = allocateStatic(DataType.ARRAY_POINTER.fixedSize);
            staticMemory.putInt(ptrAddress, dynamicAddress);
            dynamicAllocations.put(dynamicAddress, new DynamicAllocation(dynamicAddress, totalSize, elementType));
            return ptrAddress;
        }
        else if(elementType == DataType.BIG_INTEGER){
            int ptrArraySize = DataType.ARRAY_POINTER.fixedSize * size;
            int dynamicAddress = allocateDynamic(ptrArraySize);
            // Инициализация нулями
            for (int i = 0; i < ptrArraySize; i++) {
                dynamicMemory.put(dynamicAddress + i, (byte) 0);
            }
            int arrayPtr = allocateStatic(DataType.ARRAY_POINTER.fixedSize);
            staticMemory.putInt(arrayPtr, dynamicAddress);
            dynamicAllocations.put(dynamicAddress, new DynamicAllocation(dynamicAddress, ptrArraySize, DataType.ARRAY_POINTER));
            return  arrayPtr;
        }
        throw new RuntimeException("Unsupported array element type: " + elementType);
    }

    public static int getArrayElementAddress(int arrayPointer, int index, DataType elementType){
        int base = staticMemory.getInt(arrayPointer);
        if(elementType.hasFixedSize())
            return base + index * elementType.fixedSize;
        else if(elementType == DataType.BIG_INTEGER)
            return base + index * DataType.ARRAY_POINTER.fixedSize;
        throw new RuntimeException("Unsupported element type: " + elementType);
    }

    // => Операции с типами
    public static void setInt(int address, int value) { staticMemory.putInt(address, value); }
    public static int getInt(int address) { return staticMemory.getInt(address); }

    public static void setDouble(int address, double value) { staticMemory.putDouble(address, value); }
    public static double getDouble(int address) { return staticMemory.getDouble(address); }

    public static void setBoolean(int address, boolean value) { staticMemory.put(address, (byte)(value ? 1 : 0)); }
    public static boolean getBoolean(int address) { return staticMemory.get(address) != 0; }

    public static void setBigInteger(int pointerAddress, BigInteger value){
        int currentDataAddress = staticMemory.getInt(pointerAddress);
        byte[] newBytes = value.toByteArray();
        int newSize = newBytes.length;

        DynamicAllocation oldAlloc = dynamicAllocations.get(currentDataAddress);

        if(oldAlloc != null && oldAlloc.size >= newSize){
            dynamicMemory.position(currentDataAddress);
            dynamicMemory.put(newBytes);
            for (int i = newSize; i < oldAlloc.size; i++) {
                dynamicMemory.put((byte) 0);
            }
            return;
        }

        if (oldAlloc != null)
            freeDynamic(currentDataAddress, oldAlloc.size);

        int newDataAddr = allocateDynamic(newSize);
        dynamicMemory.position(newDataAddr);
        dynamicMemory.put(newBytes);
        staticMemory.putInt(pointerAddress, newDataAddr);
        dynamicAllocations.put(newDataAddr, new DynamicAllocation(newDataAddr, newSize, DataType.BIG_INTEGER));
    }

    public static BigInteger getBigInteger(int pointerAddress) {
        int dataAddr = staticMemory.getInt(pointerAddress);
        DynamicAllocation alloc = dynamicAllocations.get(dataAddr);

        if (alloc == null || alloc.type != DataType.BIG_INTEGER)
            throw new RuntimeException("Invalid BigInteger pointer");

        byte[] bytes = new byte[alloc.size];
        dynamicMemory.position(dataAddr);
        dynamicMemory.get(bytes);
        return new BigInteger(bytes);
    }

    // => Работа с элементами массива
    public static void setArrayElementInt(int arrayPointer, int index, int value) {
        int addr = getArrayElementAddress(arrayPointer, index, DataType.INT);
        dynamicMemory.putInt(addr, value);
    }

    public static int getArrayElementInt(int arrayPointer, int index) {
        return dynamicMemory.getInt(getArrayElementAddress(arrayPointer, index, DataType.INT));
    }

    public static void setArrayElementDouble(int arrayPointer, int index, double value) {
        int addr = getArrayElementAddress(arrayPointer, index, DataType.DOUBLE);
        dynamicMemory.putDouble(addr, value);
    }

    public static double getArrayElementDouble(int arrayPointer, int index) {
        return dynamicMemory.getDouble(getArrayElementAddress(arrayPointer, index, DataType.DOUBLE));
    }

    public static void setArrayElementBoolean(int arrayPointer, int index, boolean value) {
        int addr = getArrayElementAddress(arrayPointer, index, DataType.BOOLEAN);
        dynamicMemory.put(addr, (byte)(value ? 1 : 0));
    }

    public static boolean getArrayElementBoolean(int arrayPointer, int index) {
        return dynamicMemory.get(getArrayElementAddress(arrayPointer, index, DataType.BOOLEAN)) != 0;
    }

    public static void setArrayElementBigInteger(int arrayPointer, int index, BigInteger value){
        int ptrAddr = getArrayElementAddress(arrayPointer, index, DataType.BIG_INTEGER);
        int currentDataAddr = dynamicMemory.getInt(ptrAddr);
        byte[] newBytes = value.toByteArray();
        int newSize = newBytes.length;

        DynamicAllocation oldAlloc = dynamicAllocations.get(currentDataAddr);
        if (oldAlloc != null && oldAlloc.size >= newSize) {
            dynamicMemory.position(currentDataAddr);
            dynamicMemory.put(newBytes);
            for (int i = newSize; i < oldAlloc.size; i++) {
                dynamicMemory.put((byte) 0);
            }
            return;
        }

        if (oldAlloc != null) {
            freeDynamic(currentDataAddr, oldAlloc.size);
        }

        int newDataAddr = allocateDynamic(newSize);
        dynamicMemory.position(newDataAddr);
        dynamicMemory.put(newBytes);
        dynamicMemory.putInt(ptrAddr, newDataAddr);
        dynamicAllocations.put(newDataAddr, new DynamicAllocation(newDataAddr, newSize, DataType.BIG_INTEGER));
    }

    public static BigInteger getArrayElementBigInteger(int arrayPointer, int index) {
        int ptrAddr = getArrayElementAddress(arrayPointer, index, DataType.BIG_INTEGER);
        int dataAddr = dynamicMemory.getInt(ptrAddr);
        if (dataAddr == 0) return BigInteger.ZERO;

        DynamicAllocation alloc = dynamicAllocations.get(dataAddr);
        if (alloc == null || alloc.type != DataType.BIG_INTEGER) {
            throw new RuntimeException("Invalid BigInteger in array at index " + index);
        }

        byte[] bytes = new byte[alloc.size];
        dynamicMemory.position(dataAddr);
        dynamicMemory.get(bytes);
        return new BigInteger(bytes);
    }

    // => различные вспомогательные методы
    public static void dumpMemory() {
        System.out.println("=== Static Memory Dump ===");
        dumpBuffer(staticMemory, STATIC_SIZE);

        System.out.println("\n=== Dynamic Memory Dump ===");
        dumpBuffer(dynamicMemory, DYNAMIC_SIZE);
    }

    private static void dumpBuffer(ByteBuffer buf, int size) {
        buf.rewind();
        for (int i = 0; i < size; i += 16) {
            System.out.printf("0x%04X: ", i);
            for (int j = 0; j < 16 && i + j < size; j++) {
                System.out.printf("%02X ", buf.get(i + j) & 0xFF);
            }
            System.out.println();
        }
    }

    public static void dumpAllocations() {
        System.out.println("=== Static Allocations ===");
        for (StaticAllocation a : staticAllocations.values()) {
            System.out.printf("Addr: 0x%04X, Size: %d, Type: %s%n", a.address, a.size, a.type);
        }

        System.out.println("=== Dynamic Allocations ===");
        for (DynamicAllocation a : dynamicAllocations.values()) {
            System.out.printf("Addr: 0x%04X, Size: %d, Type: %s%n", a.address, a.size, a.type);
        }

        System.out.println("=== Free Blocks ===");
        for (FreeBlock b : freeBlocks) {
            System.out.printf("Addr: 0x%04X, Size: %d%n", b.address, b.size);
        }
    }


    public static void free(int pointer) {
        int address = staticMemory.getInt(pointer);
        DynamicAllocation allocation = dynamicAllocations.get(address);

        if (allocation != null) {
            freeDynamic(allocation.address, allocation.size);
            dynamicAllocations.remove(allocation.address);
            staticMemory.putInt(pointer, 0);
            return;
        }

        throw new RuntimeException("Ошибка очистки памяти: указатель 0x" + Integer.toHexString(pointer) +
                " не ссылается на активный блок (адрес данных: 0x" + Integer.toHexString(address) + ")");
    }

    public static MemoryStats getMemoryStats() {
        int usedDynamic = 0, freeDynamic = 0, fragments = freeBlocks.size();
        int largestFree = 0;

        for (FreeBlock block : freeBlocks) {
            freeDynamic += block.size;
            if (block.size > largestFree) largestFree = block.size;
        }
        usedDynamic = DYNAMIC_SIZE - freeDynamic;

        return new MemoryStats(
                STATIC_SIZE, staticNextAddress,
                DYNAMIC_SIZE, usedDynamic, freeDynamic,
                fragments, largestFree
        );
    }

    public static class MemoryStats {
        public final int staticTotal, staticUsed;
        public final int dynamicTotal, dynamicUsed, dynamicFree;
        public final int freeBlockCount, largestFreeBlock;

        public MemoryStats(int stTotal, int stUsed, int dynTotal, int dynUsed, int dynFree, int fragCount, int largestFree) {
            this.staticTotal = stTotal; this.staticUsed = stUsed;
            this.dynamicTotal = dynTotal; this.dynamicUsed = dynUsed; this.dynamicFree = dynFree;
            this.freeBlockCount = fragCount; this.largestFreeBlock = largestFree;
        }

        @Override
        public String toString() {
            return String.format("Static: %d/%d used | Dynamic: %d/%d used, %d free blocks, largest: %d",
                    staticUsed, staticTotal, dynamicUsed, dynamicTotal, freeBlockCount, largestFreeBlock);
        }
    }

    public static void testMemoryManager() {
        setAllocationStrategy(AllocationStrategy.BEST_FIT);

        Random random = new Random();
        int freeSize = 8192;
        ArrayList<Integer> arrays = new ArrayList<>();
        for(int i = 0; i < 200; i += 1) {
            int size = random.nextInt(1, 100);
            if(freeSize - size <= 0) break;
            freeSize -= size;
            int ptr = Memory.allocateArray(Memory.DataType.INT, size);
            arrays.add(ptr);

            for(int j = 0; j < size; j++) {
                Memory.setArrayElementInt(ptr, j, j * 10);
            }
        }

    }


    public static void dumpDynamicMemorySimple() {
        System.out.println("\n=== Динамическая память ===");
        System.out.printf("%-10s | %-8s | %-10s | %s%n", "Начало", "Размер", "Статус", "Тип/Инфо");
        System.out.println("-----------|----------|------------|------------|");

        List<MemBlock> allBlocks = new ArrayList<>();

        for (DynamicAllocation a : dynamicAllocations.values())
            allBlocks.add(new MemBlock(a.address, a.size, true, a.type.name()));

        for (FreeBlock b : freeBlocks)
            allBlocks.add(new MemBlock(b.address, b.size, false, "FREE"));


        allBlocks.sort(Comparator.comparingInt(b -> b.start));

        for (MemBlock b : allBlocks) {
            String status = b.occupied ? "[USED]" : "[FREE]";
            int barLen = Math.max(1, b.size / 1000);
            String bar = b.occupied ? "█".repeat(barLen) : "·".repeat(barLen);

            System.out.printf("0x%04X     | %-8d | %-10s | %s %s%n",
                    b.start, b.size, status, bar, b.info);
        }

        System.out.println("-----------|----------|------------|------------|");
    }

    private static class MemBlock {
        int start;
        int size;
        boolean occupied;
        String info;

        MemBlock(int s, int sz, boolean occ, String inf) {
            this.start = s;
            this.size = sz;
            this.occupied = occ;
            this.info = inf;
        }
    }
}