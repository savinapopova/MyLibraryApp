async function renderData() {
    const  starsReviewContainer = document.getElementById('starsReview');
    let {reviews, rating} = await fetchReviews();


    let fullStars = 0;
    let halfStars = 0;
    let emptyStars = 0;

    if (rating !== undefined && rating > 0 && rating <= 5) {
        for (let i = 0; i <= 4; i++) {
            if (rating - 1 >= 0) {
                fullStars = fullStars + 1;
                rating = rating - 1;
            } else if (rating === 0.5) {
                halfStars = halfStars + 1;
                rating = rating - 0.5;
            } else if (rating === 0) {
                emptyStars = emptyStars + 1;
            } else {
                break;
            }
        }
    } else {
        emptyStars = 5;
    }


    for (let i = 0; i < fullStars; i++) {
        const fullStar = document.createElement('i');
        fullStar.classList.add('fa-solid');
        fullStar.classList.add('fa-star');

        fullStar.setAttribute('fill', 'currentColor');
        fullStar.setAttribute('style', 'color: gold');
        fullStar.style.fontSize = '16px';
        starsReviewContainer.appendChild(fullStar);
    }

    for (let i = 0; i < halfStars; i++) {
        const halfStar = document.createElement('i');
        halfStar.classList.add('fa-solid');
        halfStar.classList.add('fa-star-half-stroke');
        halfStar.setAttribute('fill', 'currentColor');
        halfStar.setAttribute('style', 'color: gold');
        halfStar.style.fontSize = '16px';
        starsReviewContainer.appendChild(halfStar);
    }

    for (let i = 0; i < emptyStars; i++) {
        const emptyStar = document.createElement('i');
        emptyStar.classList.add('fa-regular');
        emptyStar.classList.add('fa-star');
        emptyStar.setAttribute('fill', 'currentColor');
        emptyStar.setAttribute('style', 'color: gold');
        emptyStar.style.fontSize = '16px';
        starsReviewContainer.appendChild(emptyStar);
    }

    const mobileStars = starsReviewContainer.cloneNode(true);
    mobileStars.id = 'mobileStars';
    let stars = document.getElementById("stars");
    stars.appendChild(mobileStars);


}
