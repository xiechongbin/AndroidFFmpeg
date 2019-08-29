package com.xiaoxie.ffmpeglib.config.waterMark;

import com.xiaoxie.ffmpeglib.config.BaseConfig;

/**
 * 添加图片水印
 * Created by xcb on 2019-08-29.
 */
public class AddImageWaterMakerConfig extends BaseConfig {
    private String imgPath;
    private Locations locations;

    public String getImgPath() {
        return imgPath == null ? "" : imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Locations getLocations() {
        return locations == null ? new Locations("10", "10") : locations;
    }

    public void setLocations(Locations locations) {
        this.locations = locations;
    }
}
