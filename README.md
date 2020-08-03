#  Deckofcards API Automated Tests

**Pre-reqisites: Make sure you have Java version = 1.8 and maven version = 3.5.X or higher**  <br>

## <a name="installation"></a> Run Tests
```bash

#Clone the repo
git clone https://github.com/yvasyliev88/deckofcards.git

# Run Tests
mvn clean test -Dapi.url=http://deckofcardsapi.com/api/

# Open Allure Report (works only in Chrome browser)
mvn allure:serve
```
