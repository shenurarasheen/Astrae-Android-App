package com.synapse.astraemobileapp.libs;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.synapse.astraemobileapp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductSamples {

    static FirebaseFirestore db;

    public static void saveProduct() {

        db = FirebaseFirestore.getInstance();

        // PROD004 - Rings
        Product product4 = Product.builder()
                .productId("PROD004")
                .title("Everlasting Love Diamond Ring")
                .description("18 KT Rose Gold (2.10 g) featuring a centerpiece 0.10 ct diamond. Elegant daily wear.")
                .price(42500)
                .stockCount(15)
                .categoryId("CAT002")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD004/image1.jpg",
                        "product-images/PROD004/image2.jpg",
                        "product-images/PROD004/image3.jpg",
                        "product-images/PROD004/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.5f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Size").values(List.of("12")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("18Kt Rose Gold")).build()
                ))
                .build();

// PROD005 - Necklace
        Product product5 = Product.builder()
                .productId("PROD005")
                .title("Glimmering Chain Necklace")
                .description("22 KT Yellow Gold (4.50 g) high-polish finish sleek chain necklace.")
                .price(68000)
                .stockCount(8)
                .categoryId("CAT001")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD005/image1.jpg",
                        "product-images/PROD005/image2.jpg",
                        "product-images/PROD005/image3.jpg",
                        "product-images/PROD005/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.2f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Length").values(List.of("18 inches")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("22Kt Gold")).build()
                ))
                .build();

// PROD006 - Bracelets
        Product product6 = Product.builder()
                .productId("PROD006")
                .title("Infinity Charm Bracelet")
                .description("14 KT White Gold (3.20 g) with infinity symbol links and lobster clasp.")
                .price(35000)
                .stockCount(12)
                .categoryId("CAT004")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD006/image1.jpg",
                        "product-images/PROD006/image2.jpg",
                        "product-images/PROD006/image3.jpg",
                        "product-images/PROD006/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.0f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Type").values(List.of("Charm Bracelet")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("14Kt White Gold")).build()
                ))
                .build();

// PROD007 - Pendants
        Product product7 = Product.builder()
                .productId("PROD007")
                .title("Heart Glow Diamond Pendant")
                .description("18 KT Yellow Gold (1.10 g) heart-shaped pendant with 0.05 ct diamond accents.")
                .price(22000)
                .stockCount(25)
                .categoryId("CAT003")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD007/image1.jpg",
                        "product-images/PROD007/image2.jpg",
                        "product-images/PROD007/image3.jpg",
                        "product-images/PROD007/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.8f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Style").values(List.of("Romantic")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("18Kt Gold")).build()
                ))
                .build();

// PROD008 - Earrings
        Product product8 = Product.builder()
                .productId("PROD008")
                .title("Midnight Sapphire Studs")
                .description("9 KT White Gold (0.85 g) with natural blue sapphires (0.40 ct total).")
                .price(18500)
                .stockCount(20)
                .categoryId("CAT005")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD008/image1.jpg",
                        "product-images/PROD008/image2.jpg",
                        "product-images/PROD008/image3.jpg",
                        "product-images/PROD008/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.3f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Stone").values(List.of("Sapphire")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("9Kt White Gold")).build()
                ))
                .build();

// PROD009 - Rings
        Product product9 = Product.builder()
                .productId("PROD009")
                .title("Mens Matte Gold Band")
                .description("22 KT Yellow Gold (5.50 g) wedding band with grooved matte finish.")
                .price(75000)
                .stockCount(5)
                .categoryId("CAT002")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD009/image1.jpg",
                        "product-images/PROD009/image2.jpg",
                        "product-images/PROD009/image3.jpg",
                        "product-images/PROD009/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.9f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Gender Type").values(List.of("Men")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("22Kt Gold")).build()
                ))
                .build();

// PROD010 - Necklace
        Product product10 = Product.builder()
                .productId("PROD010")
                .title("Pearl Drop Choker")
                .description("14 KT Rose Gold (2.80 g) with a single freshwater pearl drop.")
                .price(29000)
                .stockCount(10)
                .categoryId("CAT001")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD010/image1.jpg",
                        "product-images/PROD010/image2.jpg",
                        "product-images/PROD010/image3.jpg",
                        "product-images/PROD010/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.6f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Stone").values(List.of("Pearl")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("14Kt Rose Gold")).build()
                ))
                .build();

// PROD011 - Bracelets
        Product product11 = Product.builder()
                .productId("PROD011")
                .title("Diamond Tennis Bracelet")
                .description("18 KT White Gold (6.20 g) with a row of diamonds totaling 1.50 ct.")
                .price(185000)
                .stockCount(3)
                .categoryId("CAT004")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD011/image1.jpg",
                        "product-images/PROD011/image2.jpg",
                        "product-images/PROD011/image3.jpg",
                        "product-images/PROD011/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(5.0f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Diamond Weight").values(List.of("1.50 ct")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("18Kt White Gold")).build()
                ))
                .build();

// PROD012 - Pendants
        Product product12 = Product.builder()
                .productId("PROD012")
                .title("Zodiac Star Pendant")
                .description("9 KT Yellow Gold (1.20 g) minimalist star pendant with etched detail.")
                .price(12500)
                .stockCount(50)
                .categoryId("CAT003")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD012/image1.jpg",
                        "product-images/PROD012/image2.jpg",
                        "product-images/PROD012/image3.jpg",
                        "product-images/PROD012/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.1f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Theme").values(List.of("Zodiac")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("9Kt Gold")).build()
                ))
                .build();

// PROD013 - Earrings
        Product product13 = Product.builder()
                .productId("PROD013")
                .title("Celestial Moon Hoops")
                .description("18 KT Rose Gold (1.80 g) small hoops with crescent moon charms.")
                .price(31000)
                .stockCount(18)
                .categoryId("CAT005")
                .sellerId("zD98tSEEsiVcTr4jn1rkjtkP2Ke2")
                .productImages(List.of(
                        "product-images/PROD013/image1.jpg",
                        "product-images/PROD013/image2.jpg",
                        "product-images/PROD013/image3.jpg",
                        "product-images/PROD013/image4.jpg"))
                .certificateImage("")
                .status(true)
                .rating(4.7f)
                .attributes(List.of(
                        Product.Attribute.builder().name("Design").values(List.of("Hoops")).build(),
                        Product.Attribute.builder().name("Metal").values(List.of("18Kt Rose Gold")).build()
                ))
                .build();

        List<Product> products = List.of(product4, product5, product6, product7, product8, product9, product10, product11, product12, product13);

        WriteBatch batch = db.batch();

        for (Product p : products) {
            DocumentReference ref = db.collection("products").document();
            batch.set(ref, p);
        }

        batch.commit();

    }
}
