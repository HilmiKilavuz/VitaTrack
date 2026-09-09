package com.example.vitatrack.ui.supplements.add_edit

/**
 * Kullanıcıya sunulacak hazır takviye listesi.
 *
 * Liste alfabetik sıralıdır. En sona "Other" eklenerek
 * kullanıcının kendi takviyesini elle girebilmesi sağlanır.
 *
 * Yeni bir takviye eklemek istersen sadece bu listeye
 * alfabetik sırayı koruyarak yeni satır eklemen yeterli.
 */
object SupplementPickerData {

    const val OTHER_OPTION = "Other"

    /** Alfabetik sıralı takviye adları listesi — son eleman her zaman "Other" */
    val supplements: List<String> = listOf(
        "Alpha Lipoic Acid",
        "Ashwagandha",
        "Astaxanthin",
        "B Complex",
        "Berberine",
        "Beta Glucan",
        "Biotin",
        "Black Seed Oil",
        "Calcium",
        "Chromium",
        "Cod Liver Oil",
        "CoQ10",
        "Creatine",
        "Curcumin",
        "DHEA",
        "Echinacea",
        "Evening Primrose Oil",
        "Fish Oil",
        "Folate",
        "Ginger",
        "Ginkgo Biloba",
        "Ginseng",
        "Glutamine",
        "Glycine",
        "Green Tea Extract",
        "Iron",
        "Krill Oil",
        "L-Carnitine",
        "L-Glutathione",
        "L-Lysine",
        "L-Theanine",
        "Lecithin",
        "Lutein",
        "Lycopene",
        "Magnesium",
        "Melatonin",
        "Milk Thistle",
        "MSM",
        "N-Acetyl Cysteine",
        "Omega-3",
        "Omega-6",
        "Pantothenic Acid",
        "Phosphatidylserine",
        "Potassium",
        "Probiotics",
        "Quercetin",
        "Resveratrol",
        "Selenium",
        "Spirulina",
        "Taurine",
        "Turmeric",
        "Ubiquinol",
        "Valerian Root",
        "Vitamin A",
        "Vitamin B1 (Thiamine)",
        "Vitamin B12",
        "Vitamin B2 (Riboflavin)",
        "Vitamin B3 (Niacin)",
        "Vitamin B6",
        "Vitamin C",
        "Vitamin D3",
        "Vitamin E",
        "Vitamin K2",
        "Zinc",
        OTHER_OPTION        // Her zaman en sonda
    )
}
