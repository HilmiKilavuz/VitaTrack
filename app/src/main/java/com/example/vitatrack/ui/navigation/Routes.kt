package com.example.vitatrack.ui.navigation

/**
 * Uygulamadaki tüm ekranların route (adres) tanımları buradadır.
 * Navigation bileşeni, ekranlar arası geçişi bu route'lar üzerinden yapar.
 *
 * Bir web sitesindeki URL yapısına benzetebiliriz:
 *   "supplement_list"    → Ana liste ekranı
 *   "add_supplement"     → Yeni ekleme ekranı
 *   "edit_supplement/5"  → ID=5 olan takviyeyi düzenleme ekranı
 */
object Routes {
    const val SUPPLEMENT_LIST = "supplement_list"
    const val ADD_SUPPLEMENT = "add_supplement"
    const val EDIT_SUPPLEMENT = "edit_supplement/{supplementId}" // {supplementId} dinamik parametre

    // Düzenleme ekranına giderken kullanılacak yardımcı fonksiyon
    fun editSupplement(id: Int) = "edit_supplement/$id"
}
