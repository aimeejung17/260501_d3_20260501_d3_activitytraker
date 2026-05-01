# FlowXP (20260501_d3_activitytraker)

**FlowXP** — *지금은 레벨업 중입니다*

안드로이드 폰에서 **인스타, 유튜브, 틱톡** 같은 SNS 사용 시간을 자동으로 읽어서, 게임처럼 **XP·레벨·스트릭**을 쌓는 연습용 앱이에요. (서버 없음, 폰 안에만 저장)

---

## 준비물 (한 번만 있으면 됨)

1. **PC** (Windows OK)  
2. **Android Studio** — [공식 다운로드](https://developer.android.com/studio) 에서 받아서 설치  
3. **안드로이드 폰** 또는 **에뮬레이터** (앱 실행 테스트용)

---

## 실행해 보는 방법 (가장 쉬운 순서)

1. **Android Studio** 를 연다.  
2. **Open** → 이 폴더 선택  
   - 경로 예: `d:\cursor\20260501_d3_activitytraker`  
3. 위에 뜨는 **Gradle Sync** (코끼리 아이콘 / “Sync Now”) 가 끝날 때까지 기다린다.  
   - 처음이면 `gradlew` 같은 게 자동으로 잡히거나, Studio가 알아서 맞춰 준다.  
4. 상단 메뉴 **Run → Run ‘app’** (초록 삼각형 ▶)  
5. **기기 선택**  
   - USB로 폰 연결했으면 폰 목록이 뜨고,  
   - 없으면 **Device Manager** 에서 가상폰(에뮬레이터)을 하나 만들어서 선택.  
6. 앱이 깔리면 **“사용 정보 접근”** 권한을 켜 달라는 화면이 나올 수 있어요.  
   - **설정으로 이동** → 목록에서 **FlowXP** 켜기 → 다시 앱으로 돌아오기.

이게 끝이에요. 대시보드에 레벨 / 오늘 사용 시간 / XP 예상치가 보이면 정상 동작입니다.

---

## 깃허브(GitHub)에 내 이름(폴더명)으로 저장소 만들기

여기서는 **당신 깃허브 계정 로그인**이 필요해서, 대신 **브라우저에서 만든 다음** PC에서 연결하면 됩니다.

### 1) 웹에서 새 저장소 만들기

1. 브라우저에서 [https://github.com/new](https://github.com/new) 열기  
2. **Repository name** 에 예를 들어 아래처럼 적기 (원하는 이름 OK):  
   - `20260501_d3_activitytraker` ← 지금 이 프로젝트 폴더 이름과 맞추고 싶을 때  
3. **Public** / **Private** 골라서  
4. **“Add a README” 는 체크하지 말기** (이미 PC에 README가 있으면 충돌 나기 쉬움)  
5. **Create repository** 클릭

### 2) PC에서 연결해서 올리기 (처음 한 번)

이미 이 폴더에서 `git init` 과 첫 커밋이 되어 있을 수 있어요. **처음부터 할 때만** 아래처럼 하면 됩니다.

Android Studio 안 **Terminal** 이나 **PowerShell** 을 열고, **이 프로젝트 폴더로 이동한 뒤** (경로는 본인 PC에 맞게):

```powershell
cd d:\cursor\20260501_d3_activitytraker
git init
git config user.name "깃허브에 쓸 닉네임"
git config user.email "깃허브 계정 이메일"
git add .
git commit -m "Initial commit: FlowXP Android MVP"
git branch -M main
```

**작성자 이름이 잘못 들어갔다면** (예: `Your Name`):

```powershell
git config user.name "본인닉네임"
git config user.email "본인이메일"
git commit --amend --reset-author --no-edit
```

깃허브에 만든 저장소 주소가 예를 들어  
`https://github.com/내아이디/20260501_d3_activitytraker.git` 이라면:

```powershell
git remote add origin https://github.com/내아이디/20260501_d3_activitytraker.git
git push -u origin main
```

- `내아이디` 는 본인 깃허브 아이디로 바꾸기.  
- 로그인 창이 뜨면 깃허브 계정으로 허용.

이미 `git init` 까지 해둔 상태면 **`git add` / `commit` / `remote` / `push`** 만 하면 됩니다.

---

## 자주 막히는 것

| 증상 | 할 일 |
|------|--------|
| Sync 가 안 됨 | 인터넷·방화벽 확인, Android Studio 업데이트 |
| Run 이 회색 | 상단에 `app` 실행 구성이 선택됐는지 확인 |
| 사용 시간이 0 | 설정에서 **사용 정보 접근** 에 FlowXP 켰는지, 앱 선택에서 추적할 SNS 체크했는지 확인 |

---

## 라이선스 / 문의

개인 학습·포트폴리오용 MVP예요. 라이선스는 따로 두지 않았으면 기본적으로 본인이 정하면 됩니다.

즐겨요.
