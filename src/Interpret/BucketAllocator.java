package Interpret;

import java.util.*;

public class BucketAllocator implements Allocator{
    private int maxSize;
    private BitSet nonEmptySizes; // Размеры непустых бакетов
    private Map<Integer, Memory.FreeBlock> blocksByStart = new HashMap<>(); // доступ по началу блока

    @SuppressWarnings("uncheked")
    private Set<Memory.FreeBlock>[] buckets; // храним все непусты бакеты, аннотация для generic array

    @SuppressWarnings("unchecked")
    public BucketAllocator(int maxSize) {
        if (maxSize <= 0)
            throw new IllegalArgumentException("maxSize must be > 0");

        this.maxSize = maxSize;
        this.nonEmptySizes = new BitSet(maxSize + 1);
        this.buckets = new LinkedHashSet[maxSize + 1];

        for (int i = 0; i <= maxSize; i++)
            buckets[i] = new LinkedHashSet<>();
    }

    @Override public void addFreeBlock(Memory.FreeBlock block){
        if(block == null || block.size <= 0) return;
        if(block.size > maxSize) throw new IllegalArgumentException("Size of block is large, maxSize = " + maxSize);

        blocksByStart.put(block.address, block);

        buckets[block.size].add(block);
        nonEmptySizes.set(block.size);
    }

    @Override public void removeFreeBlock(Memory.FreeBlock block){
        if(block == null || block.size <= 0) return;
        if(block.size > maxSize) throw new IllegalArgumentException("Size of block is large, maxSize = " + maxSize);

        blocksByStart.remove(block.address);

        Set<Memory.FreeBlock> bucket = buckets[block.size];

        bucket.remove(block);

        if(bucket.isEmpty())
            nonEmptySizes.clear(block.size);
    }

    @Override public Memory.FreeBlock findFirstFit(int size){
        // Заглушка по сути, ибо нормальный first-fit написать пока нет мыслей как
        return findBestFit(size);
    }

    @Override public Memory.FreeBlock findBestFit(int size){
        if (size <= 0 || size > maxSize) return null;

        int bucketSize = nonEmptySizes.nextSetBit(maxSize);

        if (bucketSize == -1 || bucketSize < size) return null;

        return buckets[bucketSize].iterator().next();
    }

    @Override public Memory.FreeBlock findWorstFit(int size){
        if (size <= 0 || size > maxSize) return null;

        int bucketSize = nonEmptySizes.nextSetBit(size);


        if (bucketSize == -1 || bucketSize < size) return null;

        return buckets[bucketSize].iterator().next();
    }

    @Override public Memory.FreeBlock findBlockByAddress(int address){
        for(var block: blocksByStart.values()){
            if (block.address <= address && address < block.address + block.size)
                return block;
        }
        return null;
    }
}
