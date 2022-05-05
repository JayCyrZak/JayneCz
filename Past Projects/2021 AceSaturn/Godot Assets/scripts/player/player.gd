extends RigidBody2D

signal game_over
signal health_changed(new_health)
signal score_changed(new_score)
signal thrust_changed(new_thrust)
signal fire_main_started
signal fire_main_stopped

const DEFAULT_DIRECTION := Vector2.RIGHT

export (float) var max_health := 100.0
export (float) var score_heal_amount := 0.1
export (float) var collision_damage := 10.0

# input strength needed to rotate when moving joystick outward
export (float, 1.0) var rotation_deadzone_out := 0.1
# input strength needed to rotate when moving joystick inward
# (rotation_deadzone_in should always be higher than rotation_deadzone_out)
export (float, 1.0) var rotation_deadzone_in := 0.8
export (float, 360.0) var rotation_accuracy := 5.0
export (float) var rotation_max_speed := 15.0
export (float) var thrust_max_speed := 1500.0
# reduces rotation speed during thrust; higher value -> lower rotation speed
export (float, 1.0) var thrust_dampens_rotation := 0.95
export (float, 1.0) var firing_dampens_rotation := 0.95
export (float, 0.0, 0.05, 0.0001) var draft_factor := 0.01

var _score_total := 0
var _rotation_goal := 0.0
var _rotation_strength := 0.0
var _thrust_strength := 0.0
var _firing := false

onready var _current_health := max_health


func _ready() -> void:
	InputMap.action_set_deadzone("player_rotate_right", rotation_deadzone_out)
	InputMap.action_set_deadzone("player_rotate_left", rotation_deadzone_out)
	InputMap.action_set_deadzone("player_rotate_down", rotation_deadzone_out)
	InputMap.action_set_deadzone("player_rotate_up", rotation_deadzone_out)


func _physics_process(delta: float) -> void:
	_rotate()
	_thrust(delta)
	_draft(delta)


func _input(event: InputEvent) -> void:
	_update_rotation_goal()
	if event.is_action("player_thrust"):
		_thrust_strength = Input.get_action_strength("player_thrust")
		emit_signal("thrust_changed", _thrust_strength)
	elif event.is_action_pressed("player_fire_main"):
		_firing = true
		emit_signal("fire_main_started")
	elif event.is_action_released("player_fire_main"):
		_firing = false
		emit_signal("fire_main_stopped")


func reset() -> void:
	_score_total = 0
	_rotation_goal = rotation
	_rotation_strength = 0.0
	_thrust_strength = 0.0
	_current_health = max_health
	visible = true
	set_process_input(true)


func take_damage(damage_amount: float) -> void:
	_current_health = min(_current_health - damage_amount, max_health)
	emit_signal("health_changed", 100.0 * _current_health / max_health)
	if _current_health <= 0.0:
		emit_signal("game_over")
		set_process_input(false)
		visible = false
		_rotation_strength = 0.0
		_thrust_strength = 0.0
		emit_signal("thrust_changed", _thrust_strength)
		_firing = false
		emit_signal("fire_main_stopped")


func gain_score() -> void:
	if _score_total == 0:
		_current_health = max_health
	_score_total += 1
	take_damage(-score_heal_amount)
	emit_signal("score_changed", _score_total)


func _on_Player_body_entered(body: Node) -> void:
	if body.is_in_group("can_take_damage"):
		body.take_damage(collision_damage)


func _rotate() -> void:
	var rotation_difference := _rotation_goal - rotation_degrees
	if rotation_difference == 0.0:
		# current rotation and goal rotation are the same, so we don't rotate
		return

	if abs(rotation_difference) > 180.0:
		rotation_difference -= 360.0 * sign(rotation_difference)

	if abs(rotation_difference) <= rotation_accuracy:
		angular_velocity = 0.0
		return

	var new_angular_velocity := rotation_max_speed
	# reduce rotation speed when thrusting/firing
	var dampener: float
	if _firing:
		dampener = max(firing_dampens_rotation, _thrust_strength * thrust_dampens_rotation)
	else:
		dampener = (_thrust_strength * thrust_dampens_rotation)
	new_angular_velocity *= 1.0 - dampener
	# if the difference is negative, we rotate the opposite way
	angular_velocity = new_angular_velocity * sign(rotation_difference)


func _thrust(delta: float) -> void:
	var direction := DEFAULT_DIRECTION.rotated(rotation).normalized()
	apply_central_impulse(direction * _thrust_strength * thrust_max_speed * delta)


func _draft(delta: float) -> void:
	var linear_speed := linear_velocity.length()
	var draft_amount := linear_speed * linear_speed * draft_factor * delta
	apply_central_impulse(linear_velocity.normalized() * -draft_amount)


func _update_rotation_goal() -> void:
	var right := Input.get_action_strength("player_rotate_right")
	var left := Input.get_action_strength("player_rotate_left")
	var down := Input.get_action_strength("player_rotate_down")
	var up := Input.get_action_strength("player_rotate_up")
	# right/down is positive x/y, left/up is negative x/y
	var goal_vector := Vector2(right - left, down - up)
	var new_input_strength := goal_vector.length()
	if new_input_strength > _rotation_strength or new_input_strength > rotation_deadzone_in:
		_rotation_goal = fmod(360.0 + rad2deg(DEFAULT_DIRECTION.angle_to(goal_vector)), 360.0)
	_rotation_strength = new_input_strength
