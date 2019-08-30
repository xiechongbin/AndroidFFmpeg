package com.xiaoxie.ffmpeglib.config.waterMark;

import com.xiaoxie.ffmpeglib.config.BaseConfig;

/**
 * 添加图片水印
 * Created by xcb on 2019-08-29.
 */
public class AddImageWaterMakerConfig extends BaseConfig {
    private String waterMakerPath;
    private Locations locations;
    private float waterMakerWidth;
    private float waterMakerHeight;
    
    public String getWaterMakerPath() {
        return waterMakerPath == null ? "" : waterMakerPath;
    }

    public void setWaterMakerPath(String imgPath) {
        this.waterMakerPath = imgPath;
    }

    public Locations getLocations() {
        return locations == null ? new Locations("10", "10") : locations;
    }

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    public float getWaterMakerWidth() {
        return waterMakerWidth;
    }

    public void setWaterMakerWidth(float waterMakerWidth) {
        this.waterMakerWidth = waterMakerWidth;
    }

    public float getWaterMakerHeight() {
        return waterMakerHeight;
    }

    public void setWaterMakerHeight(float waterMakerHeight) {
        this.waterMakerHeight = waterMakerHeight;
    }
}
