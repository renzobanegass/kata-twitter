package katatwitterrenzo.core.domain.user

class UserNotFoundException(nickname: String) : RuntimeException("Could not find user with nickname $nickname")