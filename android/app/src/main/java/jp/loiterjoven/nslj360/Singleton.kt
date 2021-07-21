package jp.loiterjoven.nslj360

class Singleton {
    companion object {
        private val ourInstance = Singleton()
        fun getInstance(): Singleton {
            return ourInstance
        }
    }
    lateinit var currentUser: UserModel
}