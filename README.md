# EMF PlayerAnimator Bridge

Client-side Forge 1.20.1 compatibility bridge for Entity Model Features (EMF) and entities animated through Player Animator's animation runtime.

## Goals

- Pause EMF animation on the body parts currently animated by Player Animator
- Resume EMF cleanly after the base entity render
- Stay generic to any `LivingEntity` implementing `IAnimatedPlayer`
- Avoid any hard dependency on Mob Player Animator

## Project layout

- `common`: loader-neutral bridge logic and client mixins
- `forge`: Forge entrypoint, config registration, and dev runs

## Current scope

This v1 targets Forge 1.20.1 only. The shared code is kept loader-neutral where practical so a later Fabric port can reuse most of the logic.
