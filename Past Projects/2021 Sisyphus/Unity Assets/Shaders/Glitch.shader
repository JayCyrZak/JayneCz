Shader "Hidden/Custom/Glitch"
{
	HLSLINCLUDE

#include "Packages/com.unity.postprocessing/PostProcessing/Shaders/StdLib.hlsl"

		//TEXTURE2D_SAMPLER2D(_MainTex, samplerwrap_MainTex);
	Texture2D _MainTex;
	SamplerState sampler_linear_repeat;
	float _intensity;
	float _blackness;

	float nrand(float2 uv)
	{
		return frac(sin(dot(uv, float2(12.9898, 78.233))) * 43758.5453);
	}

	float Rescale(float min_value, float max_value, float value) 
	{
		return saturate((value - min_value) / (max_value - min_value));
	}


	float4 Frag(VaryingsDefault i) : SV_Target
	{
		float2 tex_coord = i.texcoord;
		float random_time_0 = nrand(float2(int(_Time.w * 2), 0)) * 1024;
		float random_time_1 = nrand(float2(_Time.w, 0)) * 512;
		float random_0 = nrand(float2(int(i.vertex.y / 64 + random_time_0), 0)) - 0.5;
		random_0 *= int(nrand(float2(int(i.vertex.y / 64 + random_time_0 * 10), 0)) + _intensity * 0.7);
		random_0 *= Rescale(0, 0.7, _intensity) * 0.45;

		float random_1 = nrand(float2(int(i.vertex.y + random_time_1), 0)) - 0.5;
		random_1 *= Rescale(0.65, 1, _intensity) * 0.25;

		tex_coord.x += (random_0 + random_1);

		float4 color = _MainTex.Sample(sampler_linear_repeat, tex_coord);
		color.rgb = lerp(color.rgb, 0, _blackness);
		return color;
	}

		ENDHLSL

		SubShader
	{
		Cull Off ZWrite Off ZTest Always

			Pass
		{
			HLSLPROGRAM

				#pragma vertex VertDefault
				#pragma fragment Frag

			ENDHLSL
		}
	}
}