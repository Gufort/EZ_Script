package Interpret;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class Memory {
    private static final int TOTAL_MEMORY = 65536;
    private static ByteBuffer memory = ByteBuffer.allocate(TOTAL_MEMORY);

    // Карта для отслеживания выделенных областей
    private static Map<Integer, MemoryBlock> allocatedBlocks = new HashMap<>();
    private static int nextAddress = 0;

    static{
        memory.order(ByteOrder.BIG_ENDIAN);
    }

    public enum DataType {
        INT(4),
        DOUBLE(8),
        BOOLEAN(1),
        BIG_INTEGER(16),
        POINTER(4); // 4 байта под указатели

        public int size;
        DataType(int size) {
            this.size = size;
        }
    }

    public static class MemoryBlock {
        public int address; // Базовый адрес
        public int size; // Размер в байтах
        public DataType type; // Тип данных

        public MemoryBlock(int address, int size, DataType type) {
            this.address = address;
            this.size = size;
            this.type = type;
        }
    }

    /// Выделение памяти для примитивных типов
    public static int allocateInt(int value){
        int address = allocate(DataType.INT.size);
        setInt(address, value);
        return address;
    }

    public static int allocateDouble(double value) {
        int address = allocate(DataType.DOUBLE.size);
        setDouble(address, value);
        return address;
    }

    public static int allocateBoolean(boolean value) {
        int address = allocate(DataType.BOOLEAN.size);
        setBoolean(address, value);
        return address;
    }

    public static int allocateBigInteger(BigInteger value) {
        int address = allocate(DataType.BIG_INTEGER.size);
        setBigInteger(address, value);
        return address;
    }

    /// Выделение массива
    public static int allocateArray(DataType elementType, int length){
        int totalSize = elementType.size * length;
        int address = allocate(totalSize);
        allocatedBlocks.put(address, new MemoryBlock(address, totalSize, elementType));

        memory.position(address);
        // Быстрая инициализация нулями
        for (int i = 0; i < totalSize; i++) {
            memory.put((byte)0);
        }
        return address;
    }

    public static int getArrayElementAddress(int arrayAddress, int index, DataType elementType) {
        return arrayAddress + index * elementType.size;
    }

    public static void setArrayElementInt(int arrayAddress, int index, int value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.INT);
        setInt(elementAddress, value);
    }

    public static int getArrayElementInt(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.INT);
        return getInt(elementAddress);
    }

    public static void setArrayElementDouble(int arrayAddress, int index, double value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.DOUBLE);
        setDouble(elementAddress, value);
    }

    public static double getArrayElementDouble(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.DOUBLE);
        return getDouble(elementAddress);
    }

    public static void setArrayElementBoolean(int arrayAddress, int index, boolean value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.BOOLEAN);
        setBoolean(elementAddress, value);
    }

    public static boolean getArrayElementBoolean(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.BOOLEAN);
        return getBoolean(elementAddress);
    }

    public static void setArrayElementBigInteger(int arrayAddress, int index, BigInteger value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.BIG_INTEGER);
        setBigInteger(elementAddress, value);
    }

    public static BigInteger getArrayElementBigInteger(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index, DataType.BIG_INTEGER);
        return getBigInteger(elementAddress);
    }

    /// Операции с памятью
    public static void setInt(int address, int value){
        memory.position(address);
        memory.putInt(value);
    }

    public static void setDouble(int address, double value){
        memory.position(address);
        memory.putDouble(value);
    }

    public static void setBoolean(int address, boolean value){
        memory.position(address);
        memory.put(value ? (byte)1 : (byte)0);
    }

    public static void setBigInteger(int address, BigInteger value) {
        memory.position(address);
        byte[] bytes = value.toByteArray();
        int bytesLength = bytes.length;

        if (bytesLength <= 16) {
            if (bytesLength < 16) {
                for (int i = 0; i < 16 - bytesLength; i++) {
                    memory.put((byte)0);
                }
            }
            memory.put(bytes, 0, bytesLength);
        } else {
            int startPos = bytesLength - 16;
            memory.put(bytes, startPos, 16);
        }
    }

    public static int getInt(int address) {
        memory.position(address);
        return memory.getInt();
    }

    public static double getDouble(int address) {
        memory.position(address);
        return memory.getDouble();
    }

    public static boolean getBoolean(int address) {
        memory.position(address);
        return memory.get() != 0;
    }

    public static BigInteger getBigInteger(int address) {
        memory.position(address);
        byte[] bytes = new byte[16];
        // Читаем сразу 16 байт
        memory.get(bytes, 0, 16);
        return new BigInteger(bytes);
    }

    /// Оптимизированные версии для часто используемых BigIntegers
    public static void setBigIntegerOptimized(int address, BigInteger value) {
        if (value.bitLength() <= 63) {
            long longValue = value.longValue();
            memory.position(address);

            if (value.signum() >= 0) {
                // Положительное число
                memory.putLong(longValue);
                memory.putLong(0L);
            } else {
                // Отрицательное число
                memory.putLong(longValue);
                memory.putLong(-1L);
            }
            return;
        }

        setBigInteger(address, value);
    }

    public static BigInteger getBigIntegerOptimized(int address) {
        memory.position(address);
        long low = memory.getLong();
        long high = memory.getLong();

        if (high == 0L) {
            return BigInteger.valueOf(low);
        } else if (high == -1L) {
            if (low < 0) {
                return BigInteger.valueOf(low);
            }
        }

        memory.position(address);
        byte[] bytes = new byte[16];
        memory.get(bytes, 0, 16);
        return new BigInteger(bytes);
    }

    private static int allocate(int size){
        if(nextAddress + size > TOTAL_MEMORY)
            throw new RuntimeException("Out of Memory!");
        int address = nextAddress;
        nextAddress += size;
        return address;
    }

    public static void dumpMemory(int start, int length) {
        System.out.println("=== Memory Dump (BIG-ENDIAN) ===");
        for (int i = start; i < start + length; i += 16) {
            System.out.printf("0x%04X: ", i);
            for (int j = 0; j < 16 && i + j < TOTAL_MEMORY; j++) {
                System.out.printf("%02X ", memory.get(i + j));
            }
            System.out.println();
        }
    }
}