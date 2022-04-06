using System;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;

[Serializable]
[PostProcess(typeof(GlitchRenderer), PostProcessEvent.AfterStack, "Custom/Glitch")]
public sealed class Glitch : PostProcessEffectSettings
{
    [Range(0f, 1f), Tooltip("Glitch effect intensity.")]
    public FloatParameter intensity = new FloatParameter { value = 0.5f };
}

public sealed class GlitchRenderer : PostProcessEffectRenderer<Glitch>
{
    public override void Render(PostProcessRenderContext context)
    {
        var sheet = context.propertySheets.Get(Shader.Find("Hidden/Custom/Glitch"));
        sheet.properties.SetFloat("_intensity", GameState.Instance.glitch_intensity);
        sheet.properties.SetFloat("_blackness", GameState.Instance.screen_blackness);
        context.command.BlitFullscreenTriangle(context.source, context.destination, sheet, 0);
    }
}