
(define (domain Sokoban)
  (:requirements :strips :typing :disjunctive-preconditions :existential-preconditions)
  (:types position box)

  (:predicates
    (player-at ?p - position)
    (box-at ?b - box ?p - position)
    (free ?p - position)
    (adjacent-up ?p1 ?p2 - position)
    (adjacent-down ?p1 ?p2 - position)
    (adjacent-left ?p1 ?p2 - position)
    (adjacent-right ?p1 ?p2 - position)
  )

  ; --- MOVE ACTIONS ---
  (:action move-up
    :parameters (?from - position ?to - position)
    :precondition (and (player-at ?from) (adjacent-up ?from ?to) (free ?to))
    :effect (and (not (player-at ?from)) (player-at ?to) (not (free ?to)) (free ?from))
  )

  (:action move-down
    :parameters (?from - position ?to - position)
    :precondition (and (player-at ?from) (adjacent-down ?from ?to) (free ?to))
    :effect (and (not (player-at ?from)) (player-at ?to) (not (free ?to)) (free ?from))
  )

  (:action move-left
    :parameters (?from - position ?to - position)
    :precondition (and (player-at ?from) (adjacent-left ?from ?to) (free ?to))
    :effect (and (not (player-at ?from)) (player-at ?to) (not (free ?to)) (free ?from))
  )

  (:action move-right
    :parameters (?from - position ?to - position)
    :precondition (and (player-at ?from) (adjacent-right ?from ?to) (free ?to))
    :effect (and (not (player-at ?from)) (player-at ?to) (not (free ?to)) (free ?from))
  )

  ; --- PUSH ACTIONS ---
  (:action push-up
    :parameters (?p-loc - position ?b-loc - position ?t-loc - position ?b - box)
    :precondition (and (player-at ?p-loc)
                       (adjacent-up ?p-loc ?b-loc)
                       (box-at ?b ?b-loc)
                       (adjacent-up ?b-loc ?t-loc)
                       (free ?t-loc))
    :effect (and (not (player-at ?p-loc))
                 (player-at ?b-loc)
                 (not (box-at ?b ?b-loc))
                 (box-at ?b ?t-loc)
                 (not (free ?t-loc))
                 (free ?p-loc))
  )

  (:action push-down
    :parameters (?p-loc - position ?b-loc - position ?t-loc - position ?b - box)
    :precondition (and (player-at ?p-loc)
                       (adjacent-down ?p-loc ?b-loc)
                       (box-at ?b ?b-loc)
                       (adjacent-down ?b-loc ?t-loc)
                       (free ?t-loc))
    :effect (and (not (player-at ?p-loc))
                 (player-at ?b-loc)
                 (not (box-at ?b ?b-loc))
                 (box-at ?b ?t-loc)
                 (not (free ?t-loc))
                 (free ?p-loc))
  )

  (:action push-left
    :parameters (?p-loc - position ?b-loc - position ?t-loc - position ?b - box)
    :precondition (and (player-at ?p-loc)
                       (adjacent-left ?p-loc ?b-loc)
                       (box-at ?b ?b-loc)
                       (adjacent-left ?b-loc ?t-loc)
                       (free ?t-loc))
    :effect (and (not (player-at ?p-loc))
                 (player-at ?b-loc)
                 (not (box-at ?b ?b-loc))
                 (box-at ?b ?t-loc)
                 (not (free ?t-loc))
                 (free ?p-loc))
  )

  (:action push-right
    :parameters (?p-loc - position ?b-loc - position ?t-loc - position ?b - box)
    :precondition (and (player-at ?p-loc)
                       (adjacent-right ?p-loc ?b-loc)
                       (box-at ?b ?b-loc)
                       (adjacent-right ?b-loc ?t-loc)
                       (free ?t-loc))
    :effect (and (not (player-at ?p-loc))
                 (player-at ?b-loc)
                 (not (box-at ?b ?b-loc))
                 (box-at ?b ?t-loc)
                 (not (free ?t-loc))
                 (free ?p-loc))
  )
)