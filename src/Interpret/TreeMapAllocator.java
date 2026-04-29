package Interpret;

import java.util.*;

public class TreeMapAllocator implements Allocator{
    private TreeMap<Integer, Deque<Memory.FreeBlock>> freeBySize = new TreeMap<>();
    private TreeMap<Integer, Memory.FreeBlock> freeByAddress = new TreeMap<>();
    private Map<Integer, Memory.FreeBlock> freeByEnd = new HashMap<>();

    private int nextFitLastAddress = 1;

    @Override public void addFreeBlock(Memory.FreeBlock block){
        if (block == null || block.size <= 0) return;

        freeByAddress.put(block.address, block);
        freeByEnd.put(block.address + block.size, block);
        freeBySize.computeIfAbsent(block.size, k -> new ArrayDeque<>()).addLast(block);
        // По сути храним по одному ключу несколько блоков одинакового размера, сортировка по размеру
    }

    @Override public void removeFreeBlock(Memory.FreeBlock block){
        if(block == null) return;

        freeByAddress.remove(block.address);
        freeByEnd.remove(block.address + block.size);

        Deque<Memory.FreeBlock> deque = freeBySize.get(block.size);
        if(deque != null){
            deque.remove(block);
            if(deque.isEmpty())
                freeBySize.remove(block.size);
        }
    }

    @Override public Memory.FreeBlock findFirstFit(int size){
        for(var block: freeByAddress.values()){
            if(block.size >= size)
                return block;
        }
        return null;
    }

    @Override public Memory.FreeBlock findBestFit(int size){
        Map.Entry<Integer, Deque<Memory.FreeBlock>> entry = freeBySize.ceilingEntry(size); // наименьший ключ, больший либо равный заданному
        if(entry == null) return null;
        return entry.getValue().peekFirst();
    }

    @Override public Memory.FreeBlock findWorstFit(int size){
        Map.Entry<Integer, Deque<Memory.FreeBlock>> entry = freeBySize.lastEntry(); // просто берем самый большой
        if (entry == null || entry.getKey() < size) return null;

        return entry.getValue().peekFirst();
    }

    @Override public Memory.FreeBlock findExactFit(int size) {
        Deque<Memory.FreeBlock> exact = freeBySize.get(size);

        if (exact != null && !exact.isEmpty())
            return exact.peekFirst();

        return findBestFit(size);
    }

    @Override public Memory.FreeBlock findNextFit(int size) {
        Map.Entry<Integer, Memory.FreeBlock> entry =
                freeByAddress.ceilingEntry(nextFitLastAddress);

        while (entry != null) {
            Memory.FreeBlock block = entry.getValue();

            if (block.size >= size) {
                nextFitLastAddress = block.address + size;
                return block;
            }

            entry = freeByAddress.higherEntry(entry.getKey());
        }

        entry = freeByAddress.firstEntry();

        while (entry != null) {
            Memory.FreeBlock block = entry.getValue();

            if (block.size >= size) {
                nextFitLastAddress = block.address + size;
                return block;
            }

            entry = freeByAddress.higherEntry(entry.getKey());
        }

        return null;
    }

    @Override public Memory.FreeBlock findSegregatedFit(int size) {
        if (size <= 0) return null;

        int classEnd = sizeClassEnd(size);

        Map.Entry<Integer, Deque<Memory.FreeBlock>> entry =
                freeBySize.ceilingEntry(size);

        if (entry != null && entry.getKey() <= classEnd)
            return entry.getValue().peekFirst();


        return findBestFit(size);
    }

    private int sizeClassEnd(int size) {
        int result = 1;

        while (result < size) {
            result <<= 1;
        }

        return result;
    }

    @Override public Memory.FreeBlock findBlockByAddress(int address){
        Map.Entry<Integer, Memory.FreeBlock> entry = freeByAddress.floorEntry(address); // Пара k,v с наибольшим ключом, меньше либо равным заданному
        if(entry != null){
            Memory.FreeBlock block = entry.getValue();
            if (block.address <= address && address < block.address + block.size) {
                return block;
            }
        }
        return null;
    }

    @Override public Memory.FreeBlock findBlockStartingAt(int address) {
        return freeByAddress.get(address);
    }

    @Override public Memory.FreeBlock findBlockEndingAt(int endAddress) {
        return freeByEnd.get(endAddress);
    }

    @Override public Collection<Memory.FreeBlock> getFreeBlocks() {
        return freeByAddress.values();
    }
}
