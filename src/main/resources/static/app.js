async function fetchBook() {
    const bookId = window.location.pathname.split('/')[2];
    const baseUrl = `http://localhost:8080/books/${bookId}`;

    try {
        const response = await fetch(baseUrl);
        if (!response.ok) {
            throw new Error('Something went wrong!');
        }

        const responseJson = await response.json();
        const loadedBook = {
            id: responseJson.id,
            title: responseJson.title,
            author: responseJson.author,
            description: responseJson.description,
            copies: responseJson.copies,
            copiesAvailable: responseJson.copiesAvailable,
            category: responseJson.category,
            image: responseJson.image
        };
        return loadedBook;
    } catch (error) {
        console.error(error);
        return null;
    }
}

// Функция за извличане на ревюта
async function fetchReviews() {
    const bookId = window.location.pathname.split('/')[2];
    const reviewsUrl = `http://localhost:8080/api/reviews/search?bookId=${bookId}`;

    try {
        const responseReviews = await fetch(reviewsUrl);
        if (!responseReviews.ok) {
            throw new Error('Something went wrong!');
        }

        const responseJsonReviews = await responseReviews.json();
        const responseData = responseJsonReviews.content;

        const loadedReviews = [];
        let weightedStarReviews = 0;

        for (const key in responseData) {
            loadedReviews.push({
                id: responseData[key].id,
                bookId: responseData[key].bookId,
                userEmail: responseData[key].userEmail,
                rating: responseData[key].rating,
                reviewDescription: responseData[key].reviewDescription,
                date: responseData[key].date
            });
            weightedStarReviews += responseData[key].rating;
        }

        let totalStars = 0;
        if (loadedReviews.length > 0) {
            const averageRating = (weightedStarReviews / loadedReviews.length).toFixed(1);
            totalStars = parseFloat(averageRating);
        }

        return { reviews: loadedReviews, totalStars };
    } catch (error) {
        console.error(error);
        return { reviews: [], totalStars: 0 };
    }
}

// Функция за показване на данните
async function renderData() {
    const bookContainer = document.getElementById('root');

    // Зареждане на книгата
    const book = await fetchBook();
    if (book) {
        const bookHtml = `
                    <div class="container d-none d-lg-block">
                        <div class="row mt-5">
                            <div class="col-sm-2 col-md-2">
                                <img src="${book.image || './path-to-default-image.jpg'}" width="226" height="349" alt="Book">
                            </div>
                            <div class="col-4 col-md-4 container">
                                <div class="ml-2">
                                    <h2>${book.title}</h2>
                                    <h5 class="text-primary">${book.author}</h5>
                                    <p class="lead">${book.description}</p>
                                    <div id="starsReview"></div>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div id="latestReviews"></div>
                    </div>
                `;
        bookContainer.innerHTML = bookHtml;
    } else {
        bookContainer.innerHTML = '<p>Could not load book data.</p>';
    }

    // Зареждане на ревюта
    const { reviews, totalStars } = await fetchReviews();
    const starsReviewContainer = document.getElementById('starsReview');
    const latestReviewsContainer = document.getElementById('latestReviews');

    if (starsReviewContainer && latestReviewsContainer) {
        starsReviewContainer.innerHTML = `<div id="starsReview">${totalStars} stars</div>`;

        if (reviews.length > 0) {
            latestReviewsContainer.innerHTML = '<h3>Latest Reviews:</h3>';
            const reviewsList = document.createElement('ul');
            reviews.forEach((review) => {
                const reviewItem = document.createElement('li');
                reviewItem.innerHTML = `<b>${review.userEmail}</b> rated it ${review.rating} stars: ${review.reviewDescription}`;
                reviewsList.appendChild(reviewItem);
            });
            latestReviewsContainer.appendChild(reviewsList);
        } else {
            latestReviewsContainer.innerHTML = '<p>No reviews available.</p>';
        }
    }
}

renderData();