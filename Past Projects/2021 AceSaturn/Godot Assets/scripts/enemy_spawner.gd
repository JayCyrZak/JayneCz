extends Node2D

signal spawns_per_minute_changed(new_rate)

export (float) var spawn_radius := 1000.0
export (float, 0.5) var spawn_acceleration := 0.1
export (String) var my_string := "sudhgkjsg"

var _enemy_scene := preload("res://scenes/enemy/Enemy.tscn")
var _start_wait_time: float

onready var _my_spawn_timer := $SpawnTimer as Timer


func _ready() -> void:
	print(get_node(".."))
	yield(get_tree().root, "ready")
	_start_wait_time = _my_spawn_timer.wait_time
	change_spawnrate(_start_wait_time)


func reset() -> void:
	change_spawnrate(_start_wait_time)


func _on_SpawnTimer_timeout() -> void:
	_spawn()


func _on_AccelerationTimer_timeout() -> void:
	change_spawnrate(lerp(_my_spawn_timer.wait_time, 0.0, spawn_acceleration))


func _spawn() -> void:
	var enemy_instance := _enemy_scene.instance() as RigidBody2D
	enemy_instance.set_player(get_parent())
	var random_direction = Vector2(randf() - 0.5, randf() - 0.5).normalized()
	# var random_direction = Vector2.RIGHT.rotated(randf() * TAU)
	enemy_instance.position = global_position + random_direction * spawn_radius
	get_tree().root.add_child(enemy_instance)


func change_spawnrate(new_wait_time: float) -> void:
	_my_spawn_timer.wait_time = new_wait_time
	emit_signal("spawns_per_minute_changed", int(60.0 / new_wait_time))
