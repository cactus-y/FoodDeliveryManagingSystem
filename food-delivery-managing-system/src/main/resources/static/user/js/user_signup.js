const nameEl = document.getElementById("name");
const emailEl = document.getElementById("email");
const nickNameEl = document.getElementById("nick_name");
const passwordEl = document.getElementById("password");
const passwordConfirmEl = document.getElementById("password_confirm");
const roadAddressEl = document.getElementById("road_address");
const detailAddressEl = document.getElementById("detail_address");

let name;
let email;
let nickName;
let password;
let passwordConfirm;
let roadAddress;
let detailAddress;

// Error Check
let existEmail = true; // 중복확인이 안된 상태에서 submit 되는걸 막기 위해
let existNickName = true; // 중복확인이 안된 상태에서 submit 되는걸 막기 위해
const nameError = document.getElementById("name_error");
const emailError = document.getElementById("email_error");
const nickNameError = document.getElementById("nickname_error");
const pwError = document.getElementById("password_error");
const pwConfirmError = document.getElementById("password_confirm_error");
const roadAddressError = document.getElementById("road_address_error");
const detailAddressError = document.getElementById("detail_address_error");

// 유효성 검사
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;

nameEl.addEventListener("blur", function () {
  const nameValue = this.value.trim();
  if (nameValue) {
    nameError.style.display = "none";
    name = nameValue;
  } else {
    nameError.style.display = "block";
  }
});

emailEl.addEventListener("blur", function () {
  const emailValue = this.value.trim();

  if (!emailValue) {
    emailError.style.display = "block";
    emailError.textContent = "이메일을 입력하세요.";
    existEmail = true;
    return;
  }

  if (!emailRegex.test(emailValue)) {
    emailError.style.display = "block";
    emailError.textContent = "올바른 이메일 형식이 아닙니다.";
    existEmail = true;
    return;
  }

  fetch("/api/users/check-email?email=" + encodeURIComponent(emailValue))
  .then(res => res.json())
  .then(data => {
    if (data) {
      emailError.style.display = "block";
      emailError.textContent = "이미 가입된 이메일입니다.";
      email = emailValue;
      existEmail = true;
    } else {
      emailError.style.display = "none";
      email = emailValue;
      existEmail = false;
    }
  });
});

nickNameEl.addEventListener("blur", function () {
  const nickNameValue = this.value.trim();

  if (!nickNameValue) {
    nickNameError.style.display = "block";
    nickNameError.textContent = "닉네임을 입력하세요.";
    existNickName = true;
    return;
  }

  fetch("/api/users/nick-name?nickName=" + encodeURIComponent(nickNameValue))
  .then(res => res.json())
  .then(data => {
    if (data) {
      nickNameError.style.display = "block";
      nickNameError.textContent = "이미 존재하는 닉네임입니다.";
      nickName = nickNameValue;
      existNickName = true;
    } else {
      nickNameError.style.display = "none";
      nickName = nickNameValue;
      existNickName = false;
    }
  });
});

passwordEl.addEventListener("blur", function (e) {
  const passwordValue = this.value.trim();

  if (!passwordValue) {
    pwError.style.display = "block"
    return;
  }

  if (!passwordRegex.test(passwordValue)) {
    pwError.style.display = "block"
    pwError.textContent = "비밀번호는 8자 이상이며 대소문자, 숫자, 특수문자를 포함해야 합니다.";
    return;
  }

  pwError.style.display = "none";
  password = passwordValue;
});

passwordConfirmEl.addEventListener("blur", function (e) {
  const passwordConfirmValue = this.value.trim();

  if (password !== passwordConfirmValue) {
    pwConfirmError.style.display = "block";
  } else {
    pwConfirmError.style.display = "none";
    passwordConfirm = passwordConfirmValue;
  }
});

roadAddressEl.addEventListener("blur", function (e) {
  const roadAddressValue = this.value.trim();

  if (roadAddressValue) {
    roadAddressError.style.display = "none";
    roadAddress = roadAddressValue;
  } else {
    roadAddressError.style.display = "block";
  }
});

detailAddressEl.addEventListener("blur", function (e) {
  const detailAddressValue = this.value.trim();

  if (detailAddressValue) {
    detailAddressError.style.display = "none";
    detailAddress = detailAddressValue;
  } else {
    detailAddressError.style.display = "block";
  }
});

document.querySelector("form").addEventListener("submit", function(e) {

  let isValid = true; // submit 할 때 모든 검사 통과 됐는지 확인
  let isNullCheckFocus = null; // 문제되는 EL 로 focus 맞춤

  if (!name) {
    nameError.style.display = "block";
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = nameEl;
  }

  if (!email) {
    emailError.style.display = "block";
    emailError.textContent = "이메일을 입력하세요.";
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = emailEl;
  }

  if (existEmail) {
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = emailEl;
  }

  if (!nickName) {
    nickNameError.style.display = "block";
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = nickNameEl;
  }

  if (existNickName) {
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = nickNameEl;
  }

  if (!password) {
    pwError.style.display = "block";
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = passwordEl;
  }

  if (password && password !== passwordConfirm) {
    pwConfirmError.style.display = "block";
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = passwordConfirmEl;
  }

  if (!roadAddress) {
    roadAddressError.style.display = "block";
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = roadAddressEl;
  }

  if (!detailAddress) {
    detailAddressError.style.display = "block";
    isValid = false;
    if (!isNullCheckFocus) isNullCheckFocus = detailAddressEl;
  }

  if (!isValid) {
    e.preventDefault();
    if (isNullCheckFocus) isNullCheckFocus.focus();
  }

});