const campingSpotList = document.getElementById('campingSpotList');
const loginFormContainer = document.getElementById('loginFormContainer');
const mainContent = document.querySelector('main');
const loginForm = document.getElementById('loginForm');

auth.onAuthStateChanged((user) => {
    if (user) {
        console.log('User is signed in:', user);
        loginFormContainer.style.display = 'none';
        mainContent.style.display = 'block';
        const campingSpotsRef = database.ref('campingSpots');
        campingSpotsRef.on('value', (snapshot) => {
            campingSpotList.innerHTML = '';
            snapshot.forEach((childSnapshot) => {
                const campingSpot = childSnapshot.val();
                const li = document.createElement('li');
                li.textContent = `${campingSpot.locationName} - ${campingSpot.description}`;
                campingSpotList.appendChild(li);
            });
        });
    } else {
        console.log('User is signed out');
        loginFormContainer.style.display = 'block';
        mainContent.style.display = 'none'; 
    }
});

loginForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    auth.signInWithEmailAndPassword(email, password)
        .then((userCredential) => {
            const user = userCredential.user;
            console.log('User signed in:', user);
        })
        .catch((error) => {
            const errorCode = error.code;
            const errorMessage = error.message;
            console.error(`Login error (${errorCode}): ${errorMessage}`);
        });
});
