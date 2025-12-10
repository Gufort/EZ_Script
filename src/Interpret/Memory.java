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
        memory.order(ByteOrder.LITTLE_ENDIAN); // инверсная запись, младший байт спереди
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
        public int elementSize; // Размер элемента в байтах
        public int length; // Количество элементов
        public DataType type; // Тип данных

        public MemoryBlock(int address, int size, int elementSize, int length, DataType type) {
            this.address = address;
            this.size = size;
            this.elementSize = elementSize;
            this.length = length;
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

    ///  Выделение массива
    public static int allocateArray(DataType elementType, int length){
        // 4 байта под тип, 4 байта под длину и 4 байта под указатель
        int headerSize = 12;
        int dataSize = elementType.size * length;
        int totalSize = headerSize + dataSize;

        int address = allocate(totalSize);
        allocatedBlocks.put(address, new MemoryBlock(address, totalSize, elementType.size, length, elementType));

        // Запись заголовка
        memory.position(address);
        memory.putInt(elementType.ordinal(), length); // тип
        memory.putInt(length); // длина
        memory.putInt(headerSize + address); // указатель на данные

        return address;
    }

    // Инициализация элементов значениями по умолчанию
    private static void initializeElement(int address, DataType dataType) {
        switch (dataType) {
            case INT: setInt(address, 0); break;
            case DOUBLE: setDouble(address, 0.0); break;
            case BOOLEAN: setBoolean(address, false); break;
            case BIG_INTEGER: setBigInteger(address, BigInteger.ZERO); break;
        }
    }

    ///  Работа с массивами
    public static int getArrayElementAddress(int arrayAddress, int index) {
        memory.position(arrayAddress);
        int type = memory.getInt(); // пропуск типа
        int length = memory.getInt(); // пропуск длины
        int ptr =  memory.getInt(); // пропуск указателя на данные

        if(index < 0 || index >= length)
            throw new RuntimeException("index out of bounds " + index);

        MemoryBlock block = allocatedBlocks.get(arrayAddress);
        if(block == null)
            throw new RuntimeException("Invalid array address");

        return ptr + index * block.elementSize;
    }

    public static int getArrayLength(int arrayAddress) {
        memory.position(arrayAddress + 4); // пропуск типа
        return memory.getInt();
    }

    public static DataType getArrayType(int arrayAddress) {
        memory.position(arrayAddress);
        int typeOrdinal = memory.getInt();
        return DataType.values()[typeOrdinal];
    }

    public static void setArrayElementInt(int arrayAddress, int index, int value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
        setInt(elementAddress, value);
    }

    public static int getArrayElementInt(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
        return getInt(elementAddress);
    }

    public static void setArrayElementDouble(int arrayAddress, int index, double value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
        setDouble(elementAddress, value);
    }

    public static double getArrayElementDouble(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
        return getDouble(elementAddress);
    }

    public static void setArrayElementBoolean(int arrayAddress, int index, boolean value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
        setBoolean(elementAddress, value);
    }

    public static boolean getArrayElementBoolean(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
        return getBoolean(elementAddress);
    }

    public static void setArrayElementBigInteger(int arrayAddress, int index, BigInteger value) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
        setBigInteger(elementAddress, value);
    }

    public static BigInteger getArrayElementBigInteger(int arrayAddress, int index) {
        int elementAddress = getArrayElementAddress(arrayAddress, index);
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
        byte[] littleEndianBytes = new byte[16];

        int len = Math.min(bytes.length, 16);
        for (int i = 0; i < len; i++) {
            littleEndianBytes[i] = bytes[bytes.length - 1 - i]; // инвертируем порядок
        }

        memory.put(littleEndianBytes);
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
        byte[] littleEndianBytes = new byte[16];
        memory.get(littleEndianBytes);

        byte[] bigEndianBytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            bigEndianBytes[i] = littleEndianBytes[15 - i];
        }

        return new BigInteger(bigEndianBytes);
    }




    private static int allocate(int size){
        if(nextAddress + size > TOTAL_MEMORY)
            throw new RuntimeException("Out of Memory!");
        int address = nextAddress;
        nextAddress += size;
        return address;
    }

    public static void reset() {
        memory.clear();
        allocatedBlocks.clear();
        nextAddress = 0;
        // Заполняем нулями
        for (int i = 0; i < TOTAL_MEMORY; i++) {
            memory.put(i, (byte)0);
        }
    }

    public static void dumpMemory(int start, int length) {
        System.out.println("=== Memory Dump ===");
        for (int i = start; i < start + length; i += 16) {
            System.out.printf("0x%04X: ", i);
            for (int j = 0; j < 16 && i + j < TOTAL_MEMORY; j++) {
                System.out.printf("%02X ", memory.get(i + j));
            }
            System.out.println();
        }
    }

}