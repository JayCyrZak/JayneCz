extends Sprite

const DEFAULT_DIRECTION := Vector2.RIGHT

export (float) var bullet_velocity := 400.0
export (float) var bullets_per_second := 20.0 setget set_bullets_per_second

var _bullet_scene := preload("res://scenes/player/Bullet.tscn")
var _timer_ready := false
var _firing := false
var _muzzle_index := 0

onready var _timer := $FireTimer as Timer
onready var _muzzles := [$LeftMuzzle, $RightMuzzle]
onready var _rigid_body := get_parent() as RigidBody2D
onready var _sound := $Sound as AudioStreamPlayer


func _ready() -> void:
	# wait until timer is ready
	yield(get_tree().root, "ready")
	_timer_ready = true
	set_bullets_per_second(bullets_per_second)


func set_bullets_per_second(new_value: float) -> void:
	bullets_per_second = new_value
	if _timer_ready:
		_timer.wait_time = 1.0 / new_value


func _on_FireTimer_timeout() -> void:
	if _firing:
		_fire()
	else:
		_timer.stop()


func _on_Player_fire_main_started() -> void:
	_firing = true
	if _timer.is_stopped():
		_fire()
		_timer.start()


func _on_Player_fire_main_stopped() -> void:
	_firing = false


func _fire() -> void:
	_sound.play()

	var bullet_instance := _bullet_scene.instance() as RigidBody2D

	bullet_instance.position = _muzzles[_muzzle_index].global_position
	bullet_instance.linear_velocity = _rigid_body.linear_velocity
	_muzzle_index = (_muzzle_index + 1) % len(_muzzles)
	var direction := DEFAULT_DIRECTION.rotated(global_rotation).normalized()
	bullet_instance.apply_central_impulse(direction * bullet_velocity)

	get_tree().root.add_child(bullet_instance)
