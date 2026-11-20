# Project memory

## Product intent & audience

- Minecraft plugin project named **HCTeams** (`HCTeams-1.0.jar`), targeting **Minecraft 1.7.10** using **vSpigot** server.
- User is a Spanish-speaking server operator maintaining/running this plugin locally.

## Brand personality & voice

- Assistant responds in **Spanish** to match the user's language.
- Tone is direct, practical, and explanatory when technical decisions matter.

## Visual design & UX direction

- N/A (Minecraft server plugin; no UI/UX design context).

## Decisions, constraints & rationale

- `.jar` files in the `lib` folder are **real plugins** (each has its own `plugin.yml`), not just libraries — can be installed directly in `plugins/`.
- Mandatory runtime dependencies: **ProtocolLib → Proton → Neutron → HCTeams** (load order handled by the server automatically).
- **WorldEdit** is an optional dependency used only if present.
- Plugin built for **1.7.10 / vSpigot** — will not run on modern Minecraft versions.
- No code changes were needed; the assistant copied the three required JARs alongside `HCTeams-1.0.jar` at project root for convenience.

## Open questions & assumptions

- Assumes the user has or can set up a 1.7.10 vSpigot server (not a modern Spigot/Paper instance).
- No explicit user `.bolt/prompt` instructions exist yet; context is inferred only from this turn.