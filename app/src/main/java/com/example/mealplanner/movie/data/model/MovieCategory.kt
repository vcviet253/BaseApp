package com.example.mealplanner.movie.data.model

enum class MovieCategory(val displayName: String, val slug: String) {
    HANH_DONG("Hành Động", "hanh-dong"),
    MIEN_TAY("Miền Tây", "mien-tay"),
    TRE_EM("Trẻ Em", "tre-em"),
    LICH_SU("Lịch Sử", "lich-su"),
    CO_TRANG("Cổ Trang", "co-trang"),
    CHIEN_TRANH("Chiến Tranh", "chien-tranh"),
    VIEN_TUONG("Viễn Tưởng", "vien-tuong"),
    //KINH_DI("Kinh Dị", "kinh-di"),
    TAI_LIEU("Tài Liệu", "tai-lieu"),
   // BI_AN("Bí Ẩn", "bi-an"),
    //PHIM_18("Phim 18+", "phim-18"),
    TINH_CAM("Tình Cảm", "tinh-cam"),
    TAM_LY("Tâm Lý", "tam-ly"),
    THE_THAO("Thể Thao", "the-thao"),
    PHIEU_LUU("Phiêu Lưu", "phieu-luu"),
    AM_NHAC("Âm Nhạc", "am-nhac"),
    GIA_DINH("Gia Đình", "gia-dinh"),
    HOC_DUONG("Học Đường", "hoc-duong"),
    HAI_HUOC("Hài Hước", "hai-huoc"),
    HINH_SU("Hình Sự", "hinh-su"),
    VO_THUAT("Võ Thuật", "vo-thuat"),
    KHOA_HOC("Khoa Học", "khoa-hoc"),
    THAN_THOAI("Thần Thoại", "than-thoai"),
    CHINH_KICH("Chính Kịch", "chinh-kich"),
    KINH_DIEN("Kinh Điển", "kinh-dien");


    companion object {
        fun fromSlug(slug: String): MovieCategory? {
            return entries.find { it.slug == slug }
        }
    }
}
