package Interpret;

import java.util.Collection;

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

    Memory.FreeBlock findExactFit(int size);
    Memory.FreeBlock findNextFit(int size);
    Memory.FreeBlock findSegregatedFit(int size);

    Memory.FreeBlock findBlockByAddress(int address);

    Memory.FreeBlock findBlockStartingAt(int address);
    Memory.FreeBlock findBlockEndingAt(int endAddress);
    Collection<Memory.FreeBlock> getFreeBlocks();
}
