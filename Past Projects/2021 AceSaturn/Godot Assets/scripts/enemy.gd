extends RigidBody2D

const DEFAULT_DIRECTION := Vector2.RIGHT

export (float) var max_health := 20.0
export (float) var collision_damage := 2.0
export (float) var missile_velocity := 2.0
export (float) var goal_radius := 500.0
export (float) var rotation_max_speed := 1.0
export (float, 360.0) var rotation_accuracy := 5.0
export (float) var min_thrust := 900.0
export (float) var max_thrust := 1900.0
export (float) var thrust_acceleration := 50
# reduces rotation speed during thrust; higher value -> lower rotation speed
export (float, 0.0, 0.05, 0.0001) var draft_factor := 0.01

var _missile_scene := preload("res://scenes/enemy/Missile.tscn")
var _rng = RandomNumberGenerator.new()
var _player: RigidBody2D setget set_player
var _relative_direction_goal: Vector2

onready var _current_health := max_health
onready var _current_thrust: float


func _ready() -> void:
	_rng.randomize()
	_relative_direction_goal = Vector2.RIGHT.rotated(randf() * TAU)
	yield(get_tree().root, "ready")
	_current_thrust = min_thrust


func _physics_process(delta: float) -> void:
	_rotate()
	_thrust(delta)
	_draft(delta)
	_acceleration(delta)


func set_player(player: RigidBody2D) -> void:
	_player = player


func take_damage(damage_amount: float) -> void:
	_current_health -= damage_amount
	if _current_health <= 0.0:
		_player.gain_score()
		queue_free()


func _on_Enemy_body_entered(body: Node) -> void:
	if body.is_in_group("can_take_damage"):
		body.take_damage(collision_damage)


func _on_FireTimer_timeout() -> void:
	_fire_missile()


func _fire_missile() -> void:
	if _player:
		var missile_instance := _missile_scene.instance() as RigidBody2D
		get_tree().root.add_child(missile_instance)
		missile_instance.position = global_position
		missile_instance.look_at(_player.global_position)
		var direction := (_player.global_position - global_position).normalized()
		missile_instance.add_central_force(direction * missile_velocity)


func _rotate() -> void:
	if _player:
		var goal_position := _player.global_position + _relative_direction_goal * goal_radius
		var angle_to_goal := get_angle_to(goal_position)

		if abs(rad2deg(angle_to_goal)) <= rotation_accuracy:
			angular_velocity = 0.0
		else:
			angular_velocity = rotation_max_speed * sign(angle_to_goal)


func _thrust(delta: float) -> void:
	var direction := DEFAULT_DIRECTION.rotated(rotation).normalized()
	apply_central_impulse(direction * _current_thrust * delta)


func _draft(delta: float) -> void:
	var linear_speed := linear_velocity.length()
	var draft_amount := linear_speed * linear_speed * draft_factor * delta
	apply_central_impulse(linear_velocity.normalized() * -draft_amount)


func _acceleration(delta: float) -> void:
	_current_thrust = move_toward(_current_thrust, max_thrust, thrust_acceleration * delta)
