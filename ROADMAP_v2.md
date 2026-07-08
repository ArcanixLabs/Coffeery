# Coffeery v2 — Roadmap & Status

## Shipped in this update (v2.1.0)
Fully documented, sourced content — no fabricated numbers (Spec §0.4, §9):

- **Equipment catalog → 24 devices.** Added the two documented-but-missing candidates from
  Spec §6.A: **Cafec Flower Dripper** and **Ibrik** (regional cezve variant, kept distinct).
  Each has ratio band, temp, grind, cup size, time label and a full timed step routine.
  Numbers trace to the bundled research report master tables (SCA / James Hoffmann /
  Barista Hustle / manufacturer guides).
- **Drinks catalog (Spec §6.B) → 16 recipes**, surfaced on a new **Drinks** tab with a
  detail screen that shows ingredients + numbered steps (a *builder*, not a ratio slider).
  Milk-based: Latte, Cappuccino, Flat White, Cortado, Macchiato, Mocha, Affogato, Americano,
  Red Eye. Regional: Cà Phê Trứng, Cà Phê Sữa Đá, Greek Frappé, Irish Coffee, Café Bombón,
  Cuban Cortadito, Viennese. Regional recipes come from the bundled encyclopedia.
- **Learn → real curriculum (Spec §6.D).** Replaced the flat card list with a
  **Chapter → Lesson hierarchy** (9 chapters, 22 multi-paragraph lessons): Coffee 101,
  Grinding, Water, Extraction Theory, Brewing Methods, Milk & Drinks, Tasting & Sensory,
  Caffeine, Equipment Deep-Dive. The interactive "How was your cup?" troubleshooter is kept.
- **Full i18n.** Every new string exists in both `values/strings.xml` (EN) and
  `values-tr/strings.xml` (TR) — 288 keys each, no hardcoded strings.

## Staged for the next update (benchmark §E P0/P1 — architectural, higher-risk)
Deliberately not bundled here so this single push builds cleanly on the first try. Each needs
a Room schema change and/or a foreground service and should land as its own verified change:

- **Background timer + ongoing notification** (survive background/screen-lock). Note: the
  timer already sets `FLAG_KEEP_SCREEN_ON`.
- **Brew log / diary** (Room): timestamp, equipment, ratio, dose, grind, roast, rating, notes.
- **Bean management** (Room): name, origin, roaster, roast date, freshness counter.
- **Settings expansion to §4.7 taxonomy**: Appearance + coffee-themed palette picker, Language,
  Timer, My Data (export/import), Backup/Restore, Notifications, About.
- **Custom grind-size free-text field** per brew.

## Explicitly out of scope (overriding the benchmark)
- **Cloud sync / Supabase.** Spec §4.5 is privacy-first, local-only (Room, no account).
  Data export/backup stays local (.zip / JSON). No network, no account.

## Icons (Spec §6.C)
Per-equipment line-art silhouettes already exist (`EquipmentIcons.kt`). New drinks currently
use the shared cup glyph; bespoke per-drink silhouettes are staged with the next tranche.
