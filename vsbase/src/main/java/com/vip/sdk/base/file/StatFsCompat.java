package com.vip.sdk.base.file;

import android.os.StatFs;

public class StatFsCompat {

	private StatFs mStat;
	
	/**
     * Construct a new StatFs for looking at the stats of the filesystem at
     * {@code path}. Upon construction, the stat of the file system will be
     * performed, and the values retrieved available from the methods on this
     * class.
     *
     * @param path path in the desired file system to stat.
     */
    public StatFsCompat(String path) {
        mStat = new StatFs(path);
    }


    /**
     * Perform a restat of the file system referenced by this object. This is
     * the same as re-constructing the object with the same file system path,
     * and the new stat values are available upon return.
     */
    public void restat(String path) {
    	mStat.restat(path);
    }

//    /**
//     * The size, in bytes, of a block on the file system. This corresponds to
//     * the Unix {@code statfs.f_bsize} field.
//     */
//    public long getBlockSize() {
//    	if (Build.VERSION.SDK_INT >= 18) {
//    		
//    	} else {
//    		
//    	}
//        return (int) mStat.f_bsize;
//    }
//
//    /**
//     * The size, in bytes, of a block on the file system. This corresponds to
//     * the Unix {@code statfs.f_bsize} field.
//     */
//    public long getBlockSizeLong() {
//        return mStat.f_bsize;
//    }
//
//    /**
//     * @deprecated Use {@link #getBlockCountLong()} instead.
//     */
//    @Deprecated
//    public int getBlockCount() {
//        return (int) mStat.f_blocks;
//    }
//
//    /**
//     * The total number of blocks on the file system. This corresponds to the
//     * Unix {@code statfs.f_blocks} field.
//     */
//    public long getBlockCountLong() {
//        return mStat.f_blocks;
//    }
//
//    /**
//     * @deprecated Use {@link #getFreeBlocksLong()} instead.
//     */
//    @Deprecated
//    public int getFreeBlocks() {
//        return (int) mStat.f_bfree;
//    }
//
//    /**
//     * The total number of blocks that are free on the file system, including
//     * reserved blocks (that are not available to normal applications). This
//     * corresponds to the Unix {@code statfs.f_bfree} field. Most applications
//     * will want to use {@link #getAvailableBlocks()} instead.
//     */
//    public long getFreeBlocksLong() {
//        return mStat.f_bfree;
//    }
//
//    /**
//     * The number of bytes that are free on the file system, including reserved
//     * blocks (that are not available to normal applications). Most applications
//     * will want to use {@link #getAvailableBytes()} instead.
//     */
//    public long getFreeBytes() {
//        return mStat.f_bfree * mStat.f_bsize;
//    }
//
//    /**
//     * @deprecated Use {@link #getAvailableBlocksLong()} instead.
//     */
//    @Deprecated
//    public int getAvailableBlocks() {
//        return (int) mStat.f_bavail;
//    }
//
//    /**
//     * The number of blocks that are free on the file system and available to
//     * applications. This corresponds to the Unix {@code statfs.f_bavail} field.
//     */
//    public long getAvailableBlocksLong() {
//        return mStat.f_bavail;
//    }
//
//    /**
//     * The number of bytes that are free on the file system and available to
//     * applications.
//     */
//    public long getAvailableBytes() {
//        return mStat.f_bavail * mStat.f_bsize;
//    }
//
//    /**
//     * The total number of bytes supported by the file system.
//     */
//    public long getTotalBytes() {
//        return mStat.f_blocks * mStat.f_bsize;
//    }
}
