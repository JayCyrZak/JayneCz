shader_type canvas_item;

uniform vec4 color_to_replace: hint_color;
uniform vec4 color_replacement: hint_color;

void fragment() {
	COLOR = texture(TEXTURE, UV);
	if (distance(COLOR, color_to_replace) < 0.1) {
		COLOR = color_replacement;
	}
}
