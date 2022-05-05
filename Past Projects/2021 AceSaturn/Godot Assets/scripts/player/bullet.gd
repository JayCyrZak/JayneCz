extends RigidBody2D

export (float) var collision_damage := 1.0


func _ready() -> void:
	pass


func _on_DeathTimer_timeout() -> void:
	queue_free()


func _on_Bullet_body_entered(body: Node) -> void:
	if body.is_in_group("can_take_damage"):
		body.take_damage(collision_damage)
	queue_free()
