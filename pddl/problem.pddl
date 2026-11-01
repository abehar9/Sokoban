(define (problem sokoban-test)
  (:domain Sokoban)

  (:objects
    p0 p1 p2 p3 p4 - position
    b0 - box
  )

  (:init
    (player-at p0)


    (box-at b0 p2)


    (free p1)
    (free p3)
    (free p4)

 
    (goal p4)


    (right p0 p1)
    (right p1 p2)
    (right p2 p3)
    (right p3 p4)

  
    (left p1 p0)
    (left p2 p1)
    (left p3 p2)
    (left p4 p3)
  )

  (:goal
    (box-at b0 p4)
  )
)
