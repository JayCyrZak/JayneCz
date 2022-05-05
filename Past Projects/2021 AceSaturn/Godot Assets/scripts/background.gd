extends TileMap


func _ready() -> void:
	pass


func _on_LeapfrogArea_body_shape_entered(
	_body_id: int, _body: Node, _body_shape: int, area_shape: int
) -> void:
	position += cell_size * _index_to_direction(area_shape)


func _index_to_direction(index: int) -> Vector2:
	return Vector2((index - 1) % 2, (index - 2) % 2)
