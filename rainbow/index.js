const colors = [
            "red",
            "orange",
            "yellow",
            "green",
            "blue",
            "indigo",
            "violet"
        ];
        let a = 2;
        setTimeout(() => {
            document.body.style.backgroundColor = colors[1];
        }, 1);
        setInterval(() => {
            document.body.style.backgroundColor = colors[a];
            a = (a + 1) % 7;
        }, 2000);