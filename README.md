# 💎 Astrae Jewells — M-Commerce Jewelry App

Astrae is a native Android M-commerce application built for the luxury gemstone and jewelry market in Sri Lanka. It connects local jewelry vendors directly with global buyers through a real-time, serverless marketplace — combining secure cloud infrastructure with native hardware and sensor-driven interactions for a true "handheld-first" shopping experience.

The platform follows a **Security-First, Serverless Hybrid Architecture**, pairing a native Android (Java) client with a Firebase cloud backend and a Next.js web Admin Panel for product approval and analytics.

---

## 📱 Project Info

| | |
|---|---|
| **Project Name** | Astrae Jewells |
| **Platform** | Android (Native) |
| **IDE** | Android Studio |
| **Language** | Java |
| **Architecture** | MVVM (Model – View – ViewModel) |
| **Backend** | Firebase (Auth, Firestore, Storage) |
| **Payment Gateway** | PayHere |
| **Admin Panel** | Next.js (web) + Firebase Admin SDK |

---

## ✨ Key Features

### 🛍️ Core Marketplace
- Dual-role system: **Customer** and **Seller**, with customers able to upgrade into sellers
- Product browsing, search, and category-based filtering (Rings, Necklaces, etc.)
- Mandatory **Digital Authenticity Certificate** on every listing for trust verification
- Admin **Approval Gatekeeper** — new products stay `pending` until approved and instantly become visible to all users once approved
- Shopping cart, checkout, and real-time order tracking (Paid → Shipped → Delivered)

### 🎬 Event Handling & Animations
- Custom touch and gesture event handling across activities and fragments (Home, Cart, Product Details)
- Auto-scrolling image carousel on the Home screen using **ViewPager2** with a **Dots Indicator**
- Swipeable **Triple-Image Gallery** for product photo browsing with smooth visual feedback
- Material 3 motion and transition styling applied across the UI for a polished, luxury feel

### 💾 Data Storage
- **Cloud Firestore** used as the primary real-time NoSQL database (Users, Products, Orders, Categories, Cart, Addresses collections)
- **Firebase Storage** for secure, scalable hosting of high-resolution product images and authenticity certificates
- Sub-collections for per-user Cart and Address data

### 🌐 Network Connection
- All network traffic encrypted via **HTTPS**
- Real-time, bi-directional data synchronization between the Android app and the Next.js Admin Panel through Firebase Firestore
- REST-based communication between the Admin Panel and Firebase using the Firebase Admin SDK
- Integrated **PayHere** payment gateway for secure online transactions

### 🔔 Notification / Broadcast-style Updates
- Real-time **Firestore snapshot listeners** broadcast live data changes (e.g., order status updates, product approval status) directly to the UI without manual refresh
- RecyclerView lists update instantly the moment the Admin Panel approves a product or an order status changes

### 📡 Receivers & Integration
- Sensor event **listeners/receivers** (via `SensorManager`) continuously monitor the accelerometer in the background while the Cart screen is active
- Firebase Authentication state listeners manage session/login integration across the app

### ⚙️ Multitasking
- Background threading used to keep the UI responsive while loading and processing high-resolution images
- Asynchronous Firebase read/write operations to avoid blocking the main UI thread
- Lifecycle-aware ViewModels ensure operations continue safely across configuration changes

### 🧩 Advanced Components
- **MVVM** architecture: `model` (Firestore-mapped Java classes with Lombok), `repository` (Firebase & sensor logic), `activity/fragment` (UI layer)
- **RecyclerView** with Google **Flexbox Layout** for responsive, adaptive product grids
- **ViewPager2**, Fragments, and Navigation components (bottom + side navigation)
- **MPAndroidChart** for seller sales analytics visualization
- **Glide** for efficient image loading, caching, and transformation
- **Lombok** to reduce boilerplate in model classes

### ☎️ Telephony
- Integrated **Telephony API** for instant, direct voice contact between buyers and sellers straight from the Seller Details screen

### 🎞️ Multimedia
- High-resolution product image galleries (4 images per listing) hosted on Firebase Storage
- Efficient image loading, caching, and transformation with **Glide**

### 📟 Sensor Controlling
- **Accelerometer-driven "Shake-to-Delete"** feature — shaking the device clears/deletes cart items after confirmation
- Continuous G-force monitoring implemented via the Android `SensorManager` API

### 🗺️ Google Maps Integration (Maps SDK & Places API)
- **Google Maps SDK** for interactive showroom location markers and navigation paths
- **Places API** for address autocomplete and precise location search
- Users can set/search their own location on the Profile screen (long-press to pin)
- Buyers can view a seller's showroom location and get directions from the Seller Location Map screen

---

## 🏗️ Tech Stack

- **Language:** Java
- **Architecture:** MVVM
- **Backend / Cloud:** Firebase (Authentication, Firestore, Storage)
- **Payments:** PayHere Android SDK
- **Maps & Location:** Google Maps SDK for Android, Places API
- **UI/UX:** Material 3 Design, ViewPager2, Dots Indicator, Google Flexbox Layout
- **Image Loading:** Glide
- **Charts:** MPAndroidChart
- **Boilerplate Reduction:** Lombok
- **Admin Panel:** Next.js + Firebase Admin SDK (separate web project)

---

## ✅ Prerequisites

Before running the project, make sure you have:

- **Android Studio** (Giraffe or newer recommended)
- **JDK 11+**
- **Android SDK** (minimum API level as configured in `build.gradle`)
- A physical Android device **or** an emulator with **Google Play Services** installed (required for Google Maps/Places and Firebase)
- A **Firebase project** (Firestore, Authentication, and Storage enabled)
- A **Google Maps API key** with Maps SDK for Android + Places API enabled
- A **PayHere merchant account** (sandbox credentials for testing)
- A stable internet connection (the app is cloud/serverless — most features require network access)

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd astrae-jewells
```

### 2. Open in Android Studio
- Launch **Android Studio**
- Select **File → Open** and choose the cloned project folder
- Wait for Gradle sync to complete (this may take a few minutes on first run)

### 3. Add Firebase Configuration
- Create/connect a project in the [Firebase Console](https://console.firebase.google.com/)
- Enable **Authentication**, **Cloud Firestore**, and **Storage**
- Download the `google-services.json` file for your Android app
- Place it inside the `app/` directory of the project

### 4. Add API Keys
- Add your **Google Maps API key** to `local.properties` or your `AndroidManifest.xml` `<meta-data>` tag as configured in the project (e.g. `MAPS_API_KEY=your_key_here`)
- Add your **PayHere merchant ID / secret** in the designated config/constants file used by the payment module

### 5. Sync & Build
```bash
./gradlew build
```
Or simply click **Sync Project with Gradle Files** and then **Build → Make Project** in Android Studio.

---

## ▶️ Running the Project

### Option A — Run on an Emulator
1. Open **Tools → Device Manager** in Android Studio
2. Click **Create Device**, select a phone profile, and choose a system image that includes **Google Play Services** (required for Maps/Places/Firebase)
3. Finish the setup and launch the emulator
4. Select the emulator from the device dropdown in the toolbar
5. Click the green **Run ▶** button (or `Shift + F10`)
6. Wait for Gradle to build and install the APK — the app will launch automatically

> ⚠️ Note: Some hardware-dependent features (Accelerometer "Shake-to-Delete", GPS-based Maps navigation) work best on a **physical device**. Emulators can simulate sensor input via **Extended Controls → Virtual Sensors**, but real accelerometer/GPS behavior is more accurate on real hardware.

### Option B — Run on a Physical Android Device
1. On your Android phone, go to **Settings → About Phone** and tap **Build Number** 7 times to enable **Developer Options**
2. Go to **Settings → Developer Options** and enable **USB Debugging**
3. Connect the device to your computer via USB
4. Allow the **USB Debugging** prompt on your phone
5. In Android Studio, select your device from the device dropdown in the toolbar
6. Click the green **Run ▶** button (or `Shift + F10`)
7. Grant the requested runtime permissions on first launch (Location, Camera/Storage, etc.) so that Maps, Sensors, and Multimedia features work correctly

---

## 📸 Screenshots

| Splash Screen | Home Screen | Product Details |
|---|---|---|
| ![splash screen](/assets/splash.jpeg) | ![home screen](/assets/home.jpeg) | ![single_product screen](/assets/single_product.jpeg) |

| Cart (Shake-to-Delete) | Checkout | Order Tracking |
|---|---|---|
| ![cart screen](/assets/user_cart.jpeg) | ![payment screen](/assets/payment_details.jpeg) | ![order screen](/assets/orders.jpeg) |

| Seller Location Map | User Profile | Add Product |
|---|---|---|
| ![seller_location screen](/assets/seller_location.jpeg) | ![user_location screen](/assets/profile.jpeg) | ![add_product screen](/assets/add_product.jpeg) |

| Seller Details | Selling Products | Seller Location |
|---|---|---|
| ![seller_details screen](/assets/seller_details.jpeg) | ![selling_products screen](/assets/selling_products.jpeg) | ![seller_location screen](/assets/seller_location.jpeg) |

| Sign In | Sign Up | Categories |
|---|---|---|
| ![signinn screen](/assets/sign_in.jpeg) | ![signup screen](/assets/sign_up.jpeg) | ![add_product screen](/assets/all_categories.jpeg) |

---

## 📂 Project Structure (MVVM)

```
app/
 ├── model/          # Java classes (Lombok) mapped to Firestore documents
 │    ├── Address, CartItem, Category, Order, Product, User
 ├── repository/     # Firebase queries + Sensor (Accelerometer) event processing
 │    ├── Analytics, Category, Home, Map, Order, Payment, Product, Profile
 ├── activity/       # Screens: SignIn, SignUp, Home, Cart, Checkout, Orders,
 │    │               Profile, ProductDetails, SellerDetails, Map, AddProduct, etc.
 └── fragment/       # Home, Category, Listing fragments
```

---

## 🗺️ Roadmap / Future Work

- Jewelry bidding system for live auctions on rare gemstones
- Real-time gold/precious-metal price tracking
- iOS app (Swift or Flutter cross-platform)
- Social login (Google, Facebook)
- Additional payment gateways (PayPal, Stripe) and cryptocurrency support
- AI chatbot support and generative AI–driven product recommendations

---

## 📚 References

- [Firebase Documentation](https://firebase.google.com/docs)
- [Next.js Documentation](https://nextjs.org/docs)
- [Google Maps Platform Documentation](https://developers.google.com/maps)
- [PayHere Knowledge Base](https://support.payhere.lk/)
- [Android App Architecture Guide (MVVM)](https://developer.android.com/topic/architecture)
- [Android Sensors Overview](https://developer.android.com/guide/topics/sensors/sensors_overview)
- [Material Design 3 for Android](https://developer.android.com/develop/ui/views/layout/google-flexbox)

---

## 👤 Author

**Shenura Rasheen**
Software Developer | BEng Student
[LinkedIn](https://linkedin.com/in/shenurarasheen/) • [GitHub](#)

---

## 📄 License

MIT License
Copyright (c) 2026 Shenura Rasheen
