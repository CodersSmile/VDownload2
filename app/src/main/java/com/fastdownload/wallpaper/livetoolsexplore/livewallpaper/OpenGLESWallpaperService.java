package com.fastdownload.wallpaper.livetoolsexplore.livewallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.fastdownload.wallpaper.livetoolsexplore.livewallpaper.opengles.GLESPlaneAnimatedRenderer;

public abstract class OpenGLESWallpaperService extends GLESWallpaperService
{
    GLESEngine _mEngine;
    @Override
    public WallpaperService.Engine onCreateEngine()
    {
        _mEngine =  new OpenGLESEngine();
        return _mEngine;
    }


    class OpenGLESEngine extends GLESEngine implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        SharedPreferences _mPrefs;
        GLSurfaceView.Renderer _mRenderer;


        @Override
        public void onCreate(SurfaceHolder sH)
        {
            super.onCreate(sH);

            _mPrefs = getSharedPreferences("Info", Context.MODE_PRIVATE);

            _mPrefs.registerOnSharedPreferenceChangeListener(this);

            if(isValidGLES())
            {
                setEGLContextClientVersion(2);
                setPreserveEGLContextOnPause(true);
                _mRenderer = getGLESRenderer();
                setRenderer(_mRenderer);
            }
        }

        private boolean isValidGLES()
        {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            assert am != null;
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            return info.reqGlEsVersion >= 0x2000;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
        {
            if(isValidGLES())
            {
                if(prefs != getSharedPreferences("Info", Context.MODE_PRIVATE)) return;
                String color = prefs.getString("color", "COLORFUL");
                ((GLESPlaneAnimatedRenderer) _mRenderer).switchColors(color);
                Float animSpeed = prefs.getFloat("animSpeed", 0.2f);
                ((GLESPlaneAnimatedRenderer) _mRenderer).changeAnimationSpeed(animSpeed);
                String motion = prefs.getString("motion", "straight");
                ((GLESPlaneAnimatedRenderer) _mRenderer).changeMotion(motion);
                boolean sensors = prefs.getBoolean("sensors", false);
                this.activateSensors(sensors);
            }
        }
    }
    abstract GLSurfaceView.Renderer getGLESRenderer();
}
