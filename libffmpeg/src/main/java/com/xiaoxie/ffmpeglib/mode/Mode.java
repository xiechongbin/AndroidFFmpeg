package com.xiaoxie.ffmpeglib.mode;

/**
 * H264三种码率控制方法
 * Created by xcb on 2019/7/30.
 */
public class Mode {
    /**
     * 默认模式
     */
    public final static int AUTO_VBR = 3;
    /**
     * 这个模式下可设置额定码率
     * VBR（Variable Bit Rate）动态比特率，其码率可以随着图像的复杂程度的不同而变化
     * 因此其编码效率比较高，Motion发生时，马赛克很少。码率控制算法根据图像内容确定使用的比特率，
     * 图像内容比较简单则分配较少的码率(似乎码字更合适)，图像内容复杂则分配较多的码字，
     * 这样既保证了质量，又兼顾带宽限制。这种算法优先考虑图像质量
     */
    public final static int VBR = 1;

    /**
     * 固定码率 CBR（Constant Bit Rate）是以恒定比特率方式进行编码，
     * 有Motion发生时，由于码率恒定，只能通过增大QP来减少码字大小，
     * 图像质量变差，当场景静止时，图像质量又变好，因此图像质量不稳定。
     * 这种算法优先考虑码率(带宽)。这个算法也算是码率控制最难的算法了，
     * 因为无法确定何时有motion发生，假设在码率统计窗口的最后一帧发生motion，
     * 就会导致该帧size变大，从而导致统计的码率大于预设的码率，
     * 也就是说每秒统计一次码率是不合理的，应该是统计一段时间内的平均码率，这样会更合理一些
     */
    public final static int CBR = 2;
}
