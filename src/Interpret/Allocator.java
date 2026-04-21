package Interpret;

public interface Allocator {
    // Стандартные функции работы со свободными блоками
    void addFreeBlock(Memory.FreeBlock block);
    void removeFreeBlock(Memory.FreeBlock block);
    default void replaceFreeBlock(Memory.FreeBlock oldBlock, Memory.FreeBlock newBlock){
        removeFreeBlock(oldBlock);
        addFreeBlock(newBlock);
    }

    Memory.FreeBlock findFirstFit(int size);
    Memory.FreeBlock findBestFit(int size);
    Memory.FreeBlock findWorstFit(int size);

    Memory.FreeBlock findBlockByAddress(int address);
}
