package com.uiza.sdkbroadcast.enums;

import android.graphics.PointF;
import android.view.View;

import com.pedro.encoder.input.gl.render.filters.AnalogTVFilterRender;
import com.pedro.encoder.input.gl.render.filters.AndroidViewFilterRender;
import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.gl.render.filters.BasicDeformationFilterRender;
import com.pedro.encoder.input.gl.render.filters.BeautyFilterRender;
import com.pedro.encoder.input.gl.render.filters.BlackFilterRender;
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender;
import com.pedro.encoder.input.gl.render.filters.BrightnessFilterRender;
import com.pedro.encoder.input.gl.render.filters.CartoonFilterRender;
import com.pedro.encoder.input.gl.render.filters.CircleFilterRender;
import com.pedro.encoder.input.gl.render.filters.ColorFilterRender;
import com.pedro.encoder.input.gl.render.filters.ContrastFilterRender;
import com.pedro.encoder.input.gl.render.filters.DuotoneFilterRender;
import com.pedro.encoder.input.gl.render.filters.EarlyBirdFilterRender;
import com.pedro.encoder.input.gl.render.filters.EdgeDetectionFilterRender;
import com.pedro.encoder.input.gl.render.filters.ExposureFilterRender;
import com.pedro.encoder.input.gl.render.filters.FireFilterRender;
import com.pedro.encoder.input.gl.render.filters.GammaFilterRender;
import com.pedro.encoder.input.gl.render.filters.GlitchFilterRender;
import com.pedro.encoder.input.gl.render.filters.GreyScaleFilterRender;
import com.pedro.encoder.input.gl.render.filters.HalftoneLinesFilterRender;
import com.pedro.encoder.input.gl.render.filters.Image70sFilterRender;
import com.pedro.encoder.input.gl.render.filters.LamoishFilterRender;
import com.pedro.encoder.input.gl.render.filters.MoneyFilterRender;
import com.pedro.encoder.input.gl.render.filters.NegativeFilterRender;
import com.pedro.encoder.input.gl.render.filters.NoFilterRender;
import com.pedro.encoder.input.gl.render.filters.PixelatedFilterRender;
import com.pedro.encoder.input.gl.render.filters.PolygonizationFilterRender;
import com.pedro.encoder.input.gl.render.filters.RGBSaturationFilterRender;
import com.pedro.encoder.input.gl.render.filters.RainbowFilterRender;
import com.pedro.encoder.input.gl.render.filters.RippleFilterRender;
import com.pedro.encoder.input.gl.render.filters.RotationFilterRender;
import com.pedro.encoder.input.gl.render.filters.SaturationFilterRender;
import com.pedro.encoder.input.gl.render.filters.SepiaFilterRender;
import com.pedro.encoder.input.gl.render.filters.SharpnessFilterRender;
import com.pedro.encoder.input.gl.render.filters.SnowFilterRender;
import com.pedro.encoder.input.gl.render.filters.SwirlFilterRender;
import com.pedro.encoder.input.gl.render.filters.TemperatureFilterRender;
import com.pedro.encoder.input.gl.render.filters.ZebraFilterRender;

public enum FilterRender {

    None(new NoFilterRender()),
    AnalogTV(new AnalogTVFilterRender()),
    AndroidView(new AndroidViewFilterRender()),
    BasicDeformation(new BasicDeformationFilterRender()),
    Beauty(new BeautyFilterRender()),
    Black(new BlackFilterRender()),
    Blur(new BlurFilterRender()),
    Brightness(new BrightnessFilterRender()),
    Cartoon(new CartoonFilterRender()),
    Circle(new CircleFilterRender()),
    Color(new ColorFilterRender()),
    Contrast(new ContrastFilterRender()),
    Duotone(new DuotoneFilterRender()),
    EarlyBird(new EarlyBirdFilterRender()),
    EdgeDetection(new EdgeDetectionFilterRender()),
    Exposure(new ExposureFilterRender()),
    Fire(new FireFilterRender()),
    Gamma(new GammaFilterRender()),
    Glitch(new GlitchFilterRender()),
    GreyScale(new GreyScaleFilterRender()),
    HalftoneLines(new HalftoneLinesFilterRender()),
    Image70s(new Image70sFilterRender()),
    Lamoish(new LamoishFilterRender()),
    Money(new MoneyFilterRender()),
    Negative(new NegativeFilterRender()),
    Pixelated(new PixelatedFilterRender()),
    Polygonization(new PolygonizationFilterRender()),
    Rainbow(new RainbowFilterRender()),
    RGBSaturation(new RGBSaturationFilterRender()),
    Ripple(new RippleFilterRender()),
    Rotation(new RotationFilterRender()),
    Saturation(new SaturationFilterRender()),
    Sepia(new SepiaFilterRender()),
    Sharpness(new SharpnessFilterRender()),
    Snow(new SnowFilterRender()),
    Swirl(new SwirlFilterRender()),
    Temperature(new TemperatureFilterRender()),
    Zebra(new ZebraFilterRender());

    private final BaseFilterRender filterRender;

    FilterRender(BaseFilterRender filterRender) {
        this.filterRender = filterRender;
    }

    public BaseFilterRender getFilterRender() {
        return filterRender;
    }

    /**
     * for {@link RotationFilterRender} or {@link AndroidViewFilterRender}
     * @param rotation of filter
     */
    public void setRotation(int rotation) {
        if (filterRender instanceof RotationFilterRender) {
            ((RotationFilterRender) filterRender).setRotation(rotation);
        } else if(filterRender instanceof AndroidViewFilterRender){
            ((AndroidViewFilterRender)filterRender).setRotation(rotation);
        }
    }
    /**
     * {@link RGBSaturationFilterRender} only
     * Saturate red, green and blue colors 0% to 100% (0.0f to 1.0f)
     */
    public void setRGBSaturation(float r, float g, float b) {
        if (filterRender instanceof RGBSaturationFilterRender) {
            ((RGBSaturationFilterRender) filterRender).setRGBSaturation(r, g, b);
        }
    }

    /**
     * For AndroidView Filter
     * @param view View
     */
    public void setView(View view){
        if(filterRender instanceof AndroidViewFilterRender){
            ((AndroidViewFilterRender)filterRender).setView(view);
        }
    }
    /**
     * {@link AndroidViewFilterRender} only
     * @param position of View
     */
    public void setPosition(Translate position){
        if(filterRender instanceof AndroidViewFilterRender){
            ((AndroidViewFilterRender)filterRender).setPosition(position.getTranslateTo());
        }
    }

    /**
     * {@link AndroidViewFilterRender} only
     * @param scale of View
     */
    public void setScale(PointF scale){
        if(filterRender instanceof AndroidViewFilterRender){
            ((AndroidViewFilterRender)filterRender).setScale(scale.x, scale.y);
        }
    }

}
