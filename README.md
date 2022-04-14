# DogDroid

An app that uses [dog-api](https://dog.ceo/dog-api/documentation/) to fetch information about dog breeds and display
them in cards.

This was originally a code challenge, but evolved into a learning exercise on Android Architecture.
<br>It is organized as follows:

## Data layer

- Repository pattern was used to abstract data sources
- Retrofit was used to fetch api data from the network
- Room was used to persist retrieved data and is the repository's single source of truth

## UI layer

- A ViewModel is used as a state holder and processor of UI events
- DogListActivity will subscribe to the ViewModel state, and display the models received in a recyclerview, as well as
  loading and error states if needed
- It also provides a Swipe-to-Refresh functionality, which will trigger a confirmation dialog to refresh data
- Refresh will be handled by the repository, which will first clean the database data then requesting from the api again
- Lastly, clicking on a dog card will toggle its favorite status, which is also handled by the repository (that updates
  the db)

## Application layer

- Initializes Koin as a Dependency Injection provider
