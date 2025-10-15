// package com.example.gara_management.util;


// import io.github.cdimascio.dotenv.Dotenv;

// public class EvnLoader {
//     public static void loadEnv() {
//         Dotenv dotenv = Dotenv.configure()
//                 .ignoreIfMissing()
//                 .load();
//         // Set properties only when a value exists to avoid NullPointerException
//         setIfPresent("DB_URL", dotenv);
//         setIfPresent("DB_USERNAME", dotenv);
//         setIfPresent("DB_PASSWORD", dotenv);
//         // System.setProperty("GMAIL_USERNAME", dotenv.get("GMAIL_USERNAME"));
//         // System.setProperty("GMAIL_PASSWORD", dotenv.get("GMAIL_PASSWORD"));
//         // System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
//         // System.setProperty("PAYOS_CLIENT_ID", dotenv.get("PAYOS_CLIENT_ID"));
//         // System.setProperty("PAYOS_API_KEY", dotenv.get("PAYOS_API_KEY"));
//         // System.setProperty("PAYOS_CHECKSUM_KEY", dotenv.get("PAYOS_CHECKSUM_KEY"));
//     }

//     private static void setIfPresent(String key, Dotenv dotenv) {
//         String value = dotenv.get(key);
//         if (value == null) {
//             // Fall back to system environment variable if dotenv didn't provide it
//             value = System.getenv(key);
//         }
//         if (value != null) {
//             System.setProperty(key, value);
//         }
//     }
// }
