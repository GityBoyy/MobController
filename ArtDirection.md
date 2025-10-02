# Mob Controller Mod – Art Direction & Design Guide  

This guide defines the **visual and UI design direction** for the *Mob Controller Mod*, ensuring a consistent, polished, and professional look across all assets. The style balances clarity, scientific precision, and clean cybernetic aesthetics.  

---

## 🎨 Overall Theme  
**Clinical Cybernetics** – a fusion of medical precision, neural technology, and industrial machinery.  

- Clean white/light gray materials  
- Exposed circuitry & holographic displays  
- Subtle pulsing energy accents  
- Professional, futuristic laboratory feel  

---

## 🔧 Block Models & Textures  

### Neural Interface Station  
- **Concept:** Advanced crafting station with exposed neural processors  
- **Design:**  
  - White/light gray casing with brushed titanium finish  
  - Holographic 3x3 crafting grid (cyan glow)  
  - Transparent side panels with animated neural circuits  
  - Pulsing cyan/blue corner lights  
  - Front status screen with energy levels  
- **Animations:** Energy pulses travel along circuits while crafting  

---

### Electrolytic Diffuser  
- **Concept:** Fluid processor using electrochemical reactions  
- **Design:**  
  - Transparent cylindrical tank with visible fluid  
  - Copper/gold electrode rods inside  
  - Dark industrial frame with vents & pipes  
  - Fluid has bubble & arc particle effects  
- **Textures:** Blue-tinted glass, metallic frame, glowing electrodes  

---

### Septic Tank (Biological Storage)  
- **Concept:** Industrial biological fluid storage  
- **Design:**  
  - Large tank with reinforced glass viewing panel  
  - Warning labels, gauges, pipes, valves  
  - Rust/wear acceptable for realism  
  - Dynamic fluid rendering inside  
- **Textures:** Industrial metal, biohazard symbols, frosted glass edges  

---

### Quartz Glass  
- High-tech transparent block  
- Subtle edge highlights & faint grid pattern  
- Optional rainbow refraction at angles  
- Connected textures for seamless appearance  

---

## 🎒 Item Textures  

### Controllers (Head-Mounted Devices)  
- Base: Sleek headband/crown with neural node  
- Tier progression:  
  - **Copper:** Exposed wiring, basic design  
  - **Iron:** Cleaner, enclosed circuits  
  - **Gold:** Elegant, holographic projectors  
  - **Diamond:** Crystalline prismatic effects  
  - **Netherite:** Dark angular design, glowing purple highlights  
- Shared elements: Neural gem, antennae/sensors, glowing accents  

---

### Controller Upgrades  
- Modular chip/circuit board design  
- Shows “from → to” material progression  
- Central processor with glowing pathways  
- Gradient color scheme per tier upgrade  

---

### Goggles  
- Sleek AR visor with cyan HUD overlay  
- Transparent cyan lenses  
- Thin frame with sensors & antennae  
- Glowing accent points  

---

### Soul Essence  
- Geometric crystal with swirling energy core  
- Purple base color (tinted per mob type)  
- Faint mob silhouette visible inside  
- Pulsing glow animation  

---

### Brain & Brain Piece  
- **Brain Piece:** Stylized crystalline lobe, pink/gray with cyan circuits  
- **Full Brain:** More intricate, glowing neural pathways in an energy field  

---

### Lithium Battery  
- Metallic casing with charge strip  
- Copper terminals  
- LED-style durability indicator  
- Minimalist sci-fi branding  

---

### Electrolyte Bottles  
- **Empty:** Clean laboratory flask, glass markings, cap with tube  
- **Filled:** Glowing blue fluid with bubbles and electric shimmer  

---

## 🖥️ GUI Design System  

### Color Palette  
- **Backgrounds:** Dark gray (#2B2D31), lighter gray panels (#36393F)  
- **Accents:** Cyan (#00D9FF), Gold (#FFB700), Orange (#FF6B35), Green (#00E676)  
- **Text:** White (#FFFFFF), Light gray (#B9BBBE)  

---

### Neural Interface Station GUI  
- 3x3 holographic crafting grid  
- Vertical energy bar (color shifts from red → yellow → green)  
- Cyan-animated progress bar with particle effects  
- Output slot highlighted with glowing effect  

---

### Electrolytic Diffuser GUI  
- Tank visualization with animated fluid  
- Electric arcs & bubble effects  
- Cyan-styled energy bar  
- Slots outlined based on item type  

---

### Monster Inventory / Control GUI  
- Mob icon + HP bar (red, pulses when low)  
- Equipment slots + Inventory space  
- Control buttons (Follow, Stay, Defend, Attack, Go Home)  
- Owner info, timer countdown, statistics panel  

---

### Tablet GUI (Data Display)  
- Scrollable mob list with stats (HP, location, state)  
- Interactive buttons: **Select / Teleport / Release**  
- Clean, minimal data layout  
- Stretch goal: map visualization  

---

## 🎭 Special Effects & Particles  

- **Neural Interface Station:** Subtle idle particles, electric arcs while crafting  
- **Controllers:** HUD overlays, aura around controlled mobs, connection lines with goggles  
- **Soul Essence:** Faint energy particles, intensifies near mob death  
- **Electrolytic Diffuser:** Bubble rise, electric arcs, steam when finished  

---

## 📐 Technical Guidelines  

- **Resolution:**  
  - Block Textures: 16×16 (optionally 32×32 for detail)  
  - Item Textures: 16×16  
  - GUI: Modular 16×16 grid system  
- **Color Depth:** Avoid pure black (#000000), ensure 4.5:1 contrast ratio  
- **Animation:** 8–16 frames, smooth interpolation  
- **Consistency:** Cyan = tech, Gold = energy, Orange = warning, Red = danger  

---

## 🎯 Key Design Principles  
- **Clarity Over Complexity:** Functions readable at a glance  
- **Scientific Aesthetic:** Professional, cybernetic, not fantasy-based  
- **Visual Feedback:** Idle vs. active states clearly distinct  
- **Accessibility:** Good contrast, colorblind-friendly palettes  
- **Vanilla-Plus:** Fits Minecraft, but polished and futuristic  
