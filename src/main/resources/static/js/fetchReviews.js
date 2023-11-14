async function fetchReviews() {
    const bookId = window.location.pathname.split('/')[2];
    const reviewsUrl = `http://localhost:8080/reviews/book/${bookId}`;

    try {
        const responseReviews = await fetch(reviewsUrl);


        const responseJsonReviews = await responseReviews.json();
        const responseData = responseJsonReviews;

        const reviews = [];
        let weightedStarReviews = 0;

        for (const key in responseData) {
            reviews.push({
                // id: responseData[key].id,
                // bookId: responseData[key].bookId,
                userEmail: responseData[key].userEmail,
                rating: responseData[key].rating,
                comment: responseData[key].comment,
                date: responseData[key].date
            });
            weightedStarReviews += responseData[key].rating;
        }

        let rating = 0;
        if (reviews.length > 0) {
            const averageRating = (Math.round((weightedStarReviews / reviews.length) * 2) / 2)
                .toFixed(1);
            rating = parseFloat(averageRating);
        }

        return {reviews, rating};
    } catch (error) {
        console.error(error);
        return {reviews: [], rating: 0};
    }
}