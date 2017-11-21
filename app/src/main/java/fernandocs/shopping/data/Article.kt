package fernandocs.shopping.data

import com.google.gson.annotations.SerializedName

data class Result(val content: List<ArticleContent>)

data class ArticleContent(@SerializedName("id") val id: String,
                          @SerializedName("name") val name: String,
                          @SerializedName("brand") val brand: Brand,
                          @SerializedName("media") val media: Media,
                          @SerializedName("units") val units: List<Unit>)

data class Brand(@SerializedName("name") val name: String)

data class Media(@SerializedName("images") val images: List<ArticleImage>)

data class ArticleImage(@SerializedName("mediumHdUrl") val mediumHdUrl: String)

data class Unit(@SerializedName("price") val price: Price)

data class Price(@SerializedName("currency") val currency: String,
                 @SerializedName("value") val value: Double,
                 @SerializedName("formatted") val formatted: String)

data class Article(val brand: String,
                   val description: String,
                   val price: String,
                   val thumbnailUrl: String)