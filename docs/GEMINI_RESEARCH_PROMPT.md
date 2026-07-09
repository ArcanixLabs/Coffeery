# Coffeery App — Deep Research Request for Gemini

## Context
I'm building Coffeery, an Android coffee brewing companion app. The app has 32 brewing methods, 40 educational lessons, and YouTube tutorial links for each method. I need you to do deep research and find better/missing content.

## What already exists (do NOT re-research this):

### 24 Standard Brewing Methods (all have working English YouTube links)
v60, chemex, kalita, frenchpress, aeropress, moka, turkish, coldbrew, siphon, espresso, clever, switch, origami, april, stagg, timemore, beehouse, cafec, ibrik, phin, colddrip, percolator, batchbrew, napoletana

### 8 Equipment-Free Methods (added, no or limited YouTube links)
cowboy, cupping, cloth, sock, decoction, papertowel, egg, improvturk

### 40 Learn Lessons (across 9 chapters)
Chapters: Basics, Grinding, Water, Extraction, Methods, Milk, Tasting, Caffeine, Equipment

### Turkish YouTube Links (need more!)
Only 9 methods have verified Turkish links: v60, chemex, frenchpress, aeropress, moka, turkish, coldbrew, espresso, ibrik. The other 15 standard methods + 8 equipment-free methods need Turkish links.

## What I need you to research:

### 1. Turkish YouTube Links for MISSING methods
Find verified working YouTube tutorials in Turkish for these methods:
- kalita, siphon, clever, switch, origami, april, stagg, timemore, beehouse, cafec, phin, colddrip, percolator, batchbrew, napoletana
- cowboy, cupping, cloth, sock, decoction, papertowel, egg, improvturk

For each: search for "turkce {method} kahve demleme" or similar queries. Return the YouTube video ID (11 characters after ?v=). Verify the video exists (URL should load without 404). If no Turkish video exists, say "NO TR VIDEO FOUND".

### 2. Better English YouTube Links
For each equipment-free method, find the BEST English tutorial video. These currently have no or weak links. Search for "{method} coffee brewing tutorial" and return the best video ID + channel name.

### 3. More Learn Lessons (20 additional)
Suggest 20 more educational topics that would deepen the coffee curriculum. Topics could include:
- Coffee roasting science
- Grinder types and burr geometry
- Pour-over kettle techniques  
- Filter paper comparison
- Coffee and health
- Sustainability and fair trade
- Seasonal coffee selection
- Home roasting basics
- Coffee varietals deep-dive
- Brew ratio experimentation
- Temperature surfing
- Espresso machine maintenance
- Iced coffee methods
- Coffee cherry to cup journey
- Fermentation in coffee processing
- Pour-over flow rate optimization

For each lesson provide: a short title (max 5 words), which existing chapter it belongs to, a 2-3 sentence body, and the Turkish translation of both title and body.

### 4. More Equipment-Free Methods (any remaining)
Are there any other no-equipment coffee brewing methods we missed? Global/traditional methods welcome. For each: name, description, ratio range, grind, temp, brew time, 3-5 step sequence, and YouTube link if available.

## Output Format
Return a clean, structured document with these sections:
```
## SECTION 1: Turkish YouTube Links for Missing Methods
[method_id]: [youtube_id] — [video_title]
...

## SECTION 2: English YouTube Links for Equipment-Free Methods
[method_id]: [youtube_id] — [channel name] — [video_title]
...

## SECTION 3: 20 New Learn Lessons
### Lesson N
Title: [title]
Title TR: [turkish_title]
Chapter: [chapter_string_res]
Body: [2-3 sentences]
Body TR: [turkish_body]
...

## SECTION 4: Additional Equipment-Free Methods
[Same format as existing methods in the app]
```

Keep it concise and implementation-ready. No fluff. Minimum words, maximum data.
