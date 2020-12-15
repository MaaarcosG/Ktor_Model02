package com.example_ktorYT

data class Item(val id: Long?, val task: String?, val complete: Boolean?){
    override fun equals(other: Any?): Boolean{
        if(other is Item){
            return other.id == id
        }
        return super.equals(other)
    }
    override fun hashCode(): Int = id. hashCode()
}
data class Error(val message: String?)
