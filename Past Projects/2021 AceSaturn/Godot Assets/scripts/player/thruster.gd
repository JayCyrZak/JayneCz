extends Node2D

# volumes always in db
export (float, -100.0, 0.0) var volume_min := -60.0
export (float, -100.0, 0.0) var volume_max := -35.0
export (float) var volume_change_speed := 15.0
export (float) var scale_speed := 1.0

var _thrust_strength := 0.0

onready var _sound := $Sound as AudioStreamPlayer


func _ready() -> void:
	pass


func _process(delta: float) -> void:
	scale.x = move_toward(scale.x, _thrust_strength, scale_speed * delta)
	scale.y = scale.x

	if _thrust_strength > 0.0 and not _sound.playing:
		_sound.play()
	var volume_goal := volume_min + (volume_max - volume_min) * _thrust_strength
	_sound.volume_db = move_toward(_sound.volume_db, volume_goal, volume_change_speed * delta)


func _on_Player_thrust_changed(strength: float) -> void:
	_thrust_strength = strength
