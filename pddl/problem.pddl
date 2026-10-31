(define (problem sokoban-test)
  (:domain Sokoban)

  (:objects
    p0 p1 p2 p3 p4 - position
    b0 - box
  )

  (:init
    ;; Player starts at p0
    (player-at p0)

    ;; Box starts at p2
    (box-at b0 p2)

    ;; Free positions
    (free p1)
    (free p3)
    (free p4)

    ;; Target position
    (goal p4)

    ;; Linear corridor: p0 -> p1 -> p2 -> p3 -> p4
    (right p0 p1)
    (right p1 p2)
    (right p2 p3)
    (right p3 p4)

    ;; Reverse directions
    (left p1 p0)
    (left p2 p1)
    (left p3 p2)
    (left p4 p3)
  )

  (:goal
    (box-at b0 p4)
  )
)
