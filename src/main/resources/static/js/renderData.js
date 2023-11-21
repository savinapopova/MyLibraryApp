async function renderData() {
    const bookContainer = document.getElementById('root');

    // Зареждане на книгата
    const book = await fetchBook();
    if (book) {
        const bookHtml = `<div>
                    <div class="container d-none d-lg-block">
                    <div class="card m-4 ps-4 shadow bg-body rounded-3">
                        <div class="row mt-5">
                            <div class=col-sm-2 col-md-2">
                                <img src="${book.image}" width="226" height="349" alt="Book">
                            </div>
                            <div class="col-4 col-md-4 container">
                                <div class="ml-2">
                                    <h2>${book.title}</h2>
                                    <h5 class="text-primary">${book.author}</h5>
                                    <div class="mt-4 mb-4" id="starsReview"></div>
                                    <p class="lead">${book.description}</p>

                                 <a type='button' class='btn second-color btn-md text-light m-4'
                                 href="http://localhost:8080/reviews/${book.id}">
                                Reach all reviews
                            </a>
                              </div>
                        </div>
                    </div>
                  </div>

                   <div class='m-3 d-flex justify-content-center align-items-center'>
                            <a type='button' class='btn main-color btn-lg text-white'
                                  href="http://localhost:8080/checkout/book/${book.id}">
                                CheckOut
                            </a>
                  </div>



                        </div>
                    <div class='container d-lg-none mt-5'>
                    <div class='d-flex justify-content-center align-items-center'>
 <img src=${book.image} width='226' height='349' alt='Book'/>
</div>
<div class="mt-4">
<div class="ml-2 d-flex flex-column justify-content-center align-items-center">
<h2>${book.title}</h2>
<h5 class='text-primary'>${book.author}</h5>
<div id="stars"></div>
<p class='lead'>${book.description}</p>
<div class='m-3 d-flex justify-content-center align-items-center'>
                            <a type='button' class='btn second-color btn-md text-white'
                                  href="http://localhost:8080/reviews/${book.id}">
                                Reach all reviews
                            </a>
                        </div>

                         </div>

                                     <div class='m-3 d-flex justify-content-center align-items-center'>
                            <a type='button' class='btn main-color btn-lg text-white'
                                  href="http://localhost:8080/checkout/book/${book.id}">
                                CheckOut
                            </a>

                        </div>
</div>

</div>
</div>

</div>
                `;
        bookContainer.innerHTML = bookHtml;
    } else {
        bookContainer.innerHTML = '<p>Could not load book data.</p>';
    }

    // // Зареждане на ревюта
    let {reviews, rating} = await fetchReviews();
    const starsReviewContainer = document.getElementById('starsReview');



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
        fullStar.style.fontSize = '24px';
        starsReviewContainer.appendChild(fullStar);
    }

    for (let i = 0; i < halfStars; i++) {
        const halfStar = document.createElement('i');
        halfStar.classList.add('fa-solid');
        halfStar.classList.add('fa-star-half-stroke');
        halfStar.setAttribute('fill', 'currentColor');
        halfStar.setAttribute('style', 'color: gold');
        halfStar.style.fontSize = '24px';
        starsReviewContainer.appendChild(halfStar);
    }

    for (let i = 0; i < emptyStars; i++) {
        const emptyStar = document.createElement('i');
        emptyStar.classList.add('fa-regular');
        emptyStar.classList.add('fa-star');
        emptyStar.setAttribute('fill', 'currentColor');
        emptyStar.setAttribute('style', 'color: gold');
        emptyStar.style.fontSize = '24px';
        starsReviewContainer.appendChild(emptyStar);
    }

    const mobileStars = starsReviewContainer.cloneNode(true);
    mobileStars.id = 'mobileStars';
    let stars = document.getElementById("stars");
    stars.appendChild(mobileStars);


}
