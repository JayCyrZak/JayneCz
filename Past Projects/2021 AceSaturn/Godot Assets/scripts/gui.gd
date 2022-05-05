extends Node

onready var _player := get_node("../Player") as RigidBody2D
onready var _enemy_spawner := get_node("../Player/EnemySpawner") as Node2D
onready var _health_bar := $HealthBar as TextureProgress
onready var _score_number := $ScoreNumber as Button
onready var _spawns_per_minute := $SpawnsPerMinute as Button
onready var _game_over_dialog := $GameOverDialog as AcceptDialog
onready var _game_start_dialog := $GameStartDialog as AcceptDialog


func _ready() -> void:
	_game_start_dialog.popup_centered()


func _on_Player_health_changed(new_health: float) -> void:
	_health_bar.value = new_health


func _on_Player_score_changed(new_score: int) -> void:
	_score_number.text = str(new_score)


func _on_Player_game_over() -> void:
	_game_over_dialog.popup_centered()


func _on_EnemySpawner_spawns_per_minute_changed(new_rate) -> void:
	_spawns_per_minute.text = str(new_rate)


func _on_GameOverDialog_confirmed() -> void:
	for enemy in get_tree().get_nodes_in_group("enemy"):
		enemy.queue_free()
	_player.reset()
	_enemy_spawner.reset()
	_health_bar.value = _health_bar.max_value
	_score_number.text = "0"
