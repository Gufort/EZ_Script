package Interpret;

import java.util.*;

public class BucketAllocator implements Allocator{
    private int maxSize;
    private int sizeOfSmall = 512;
    private BitSet nonEmptySizes; // Размеры непустых бакетов
    private Map<Integer, Memory.FreeBlock> blocksByStart = new HashMap<>(); // доступ по началу блока
    private Map<Integer, Memory.FreeBlock> blocksByEnd = new HashMap<>(); // доступ по концу блока

    @SuppressWarnings("unchecked")
    private Set<Memory.FreeBlock>[] buckets; // храним все непусты бакеты, аннотация для generic array
    private TreeMap<Integer, Set<Memory.FreeBlock>> largeBlocks;

    private int nextFitLastAddress = 1;

    @SuppressWarnings("unchecked")
    public BucketAllocator(int maxSize) {
        this.maxSize = maxSize;
        if (maxSize <= 0)
            throw new IllegalArgumentException("maxSize must be > 0");

        this.nonEmptySizes = new BitSet(sizeOfSmall + 1);
        this.buckets = new LinkedHashSet[sizeOfSmall + 1];

        for (int i = 0; i <= sizeOfSmall; i++)
            buckets[i] = new LinkedHashSet<>();

        largeBlocks = new TreeMap<>();
    }

    @Override public void addFreeBlock(Memory.FreeBlock block){
        if(block == null || block.size <= 0) return;
        if(block.size > maxSize) throw new IllegalArgumentException("Size of block is large, maxSize = " + maxSize);

        blocksByStart.put(block.address, block);
        blocksByEnd.put(block.address + block.size, block);

        if(block.size <= sizeOfSmall) {
            buckets[block.size].add(block);
            nonEmptySizes.set(block.size);
        }
        else largeBlocks.computeIfAbsent(block.size, k -> new LinkedHashSet<>()).add(block);
    }

    @Override public void removeFreeBlock(Memory.FreeBlock block){
        if(block == null || block.size <= 0) return;
        if(block.size > maxSize) throw new IllegalArgumentException("Size of block is large, maxSize = " + maxSize);

        blocksByStart.remove(block.address);
        blocksByEnd.remove(block.address + block.size);
        if(block.size <= sizeOfSmall) {
            Set<Memory.FreeBlock> bucket = buckets[block.size];

            bucket.remove(block);

            if (bucket.isEmpty())
                nonEmptySizes.clear(block.size);
        }
        else{
            Set<Memory.FreeBlock> set = largeBlocks.get(block.size);
            if(set != null){
                set.remove(block);
                if(set.isEmpty())
                    largeBlocks.remove(block.size);
            }
        }
    }

    @Override public Memory.FreeBlock findFirstFit(int size){
        if (size <= 0 || size > maxSize) return null;

        Memory.FreeBlock bestByAddress = null;
        for (var block : blocksByStart.values()) {
            if (block.size >= size &&
                    (bestByAddress == null || block.address < bestByAddress.address)) {
                bestByAddress = block;
            }
        }

        return bestByAddress;
    }

    @Override public Memory.FreeBlock findBestFit(int size){
        if (size <= 0 || size > maxSize) return null;

        if(size <= sizeOfSmall){
            int s = nonEmptySizes.nextSetBit(size);
            if(s != -1 && s <= sizeOfSmall)
                return  buckets[s].iterator().next();
        }

        var entry = largeBlocks.ceilingEntry(size);
        if (entry != null) {
            return entry.getValue().iterator().next();
        }

        return null;
    }

    @Override public Memory.FreeBlock findWorstFit(int size){
        if (size <= 0 || size > maxSize) return null;

        var entry = largeBlocks.lastEntry();
        if (entry != null && entry.getKey() >= size)
            return entry.getValue().iterator().next();

        int s = nonEmptySizes.previousSetBit(sizeOfSmall);
        if (s >= size)
            return buckets[s].iterator().next();

        return null;
    }

    @Override public Memory.FreeBlock findExactFit(int size){
        if (size <= 0 || size > maxSize) return null;

        if (size <= sizeOfSmall) {
            if (!buckets[size].isEmpty())
                return buckets[size].iterator().next();
        }
        else {
            var set = largeBlocks.get(size);
            if (set != null && !set.isEmpty())
                return set.iterator().next();
        }

        return findBestFit(size);
    }

    @Override public Memory.FreeBlock findNextFit(int size){
        if (size <= 0 || size > maxSize) return null;

        Memory.FreeBlock candidate = null;

        for(var block: blocksByStart.values()){
            if(block.address >= nextFitLastAddress && block.size >= size)
                if(candidate == null || block.address < candidate.address)
                    candidate = block;
        }

        if (candidate == null) {
            for (Memory.FreeBlock block : blocksByStart.values()) {
                if (block.size >= size)
                    if (candidate == null || block.address < candidate.address)
                        candidate = block;

            }
        }

        if (candidate != null)
            nextFitLastAddress = candidate.address + size;

        return candidate;
    }

    @Override public Memory.FreeBlock findSegregatedFit(int size) {
        return findBestFit(size);
    }

    private int sizeClassEnd(int size) {
        int result = 1;

        while (result < size && result < maxSize) {
            result <<= 1;
        }

        return Math.min(result, maxSize);
    }

    @Override public Memory.FreeBlock findBlockByAddress(int address){
        for (var block : blocksByStart.values())
            if (block.address <= address && address < block.address + block.size)
                return block;

        return null;

    }

    @Override public Memory.FreeBlock findBlockStartingAt(int address) {
        return blocksByStart.get(address);
    }

    @Override public Memory.FreeBlock findBlockEndingAt(int endAddress) {
        return blocksByEnd.get(endAddress);
    }

    @Override public Collection<Memory.FreeBlock> getFreeBlocks() {
        return blocksByStart.values();
    }
}
