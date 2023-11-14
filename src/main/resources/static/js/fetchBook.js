async function fetchBook() {
    const bookId = window.location.pathname.split('/')[2];
    const baseUrl = `http://localhost:8080/books/getDetails/${bookId}`;

    try {
        const response = await fetch(baseUrl);

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


