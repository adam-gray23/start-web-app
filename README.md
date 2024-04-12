# START

START was developed as a learning tool for new programmers to begin their
programming journey in 2023. It is a simple, easy to use, and easy to learn language,
developed by ourselves, Niall Kelly and Adam Gray, for our third year project in Dublin
City University.

This year for our fourth year project, we wanted to continue our development of the START
eco-system by developing a START Web Applcation for users to be able learn about START
and programming concepts, write START code in a START-focused IDE, debug their code with a
built in debugger, and practice with problems designed to test new programmers ability to
solve problems incrementally.

For more information on the development of start, and how it progressed, please see
the following link: (link here)

## Table of Contents

- [Introduction](#introduction)
  - [What is START?](#what-is-start)
  - [Why use the START Web Application?](#why-start)
  - [Get in touch](#get-in-touch)
- [How to Run the Web App](#how-to-run-the-web-app)
- [How to Use the Web App](#how-to-use-the-web-app)


## Introduction

### What is START?

As previously mentioned, START is a simple, easy to use, and easy to learn, meaning
we aimed to make it so a first time programmer could pick up the language and within
a very short space of time, be able to fully use all of START's features.

START was released on GitHub in 2023 and was developed using Java.

The START Web Application was developed using the Django Framework for Python, along
with using Java to develop the START debugger. As well as this, the frontend of the
application made use of Bootstrap and JavaScript to help mold the design.

### Why START?

Why, or more appropriately, when should someone use START? Well, START is a great for
someone with no programming experience, or someone who has a little experience with a visual
programming language such as Scratch. It is also a great language for someone who wants to
further their problem solving skills, and learn how to think like a programmer.

The web app offers the following features to users:

- A syntax sensitve text editor
- A debugger
- A Memory Map to track variable values
- Code saving and loading functionality
- User accounts
- Learning resources
- Practice problems and solutions

### Get in touch

If you have any questions regarding START, or would like to get in touch for any reason,
please feel free to email either of us at:

- Niall Kelly: 02nkelly@gmail.com

- Adam Gray: adamgray8432@mail.com

## How to Run the Web App

To run the web app you must firstly:

- Be on either Windows 10 or 11
- Install all packages in requirements.txt in the root directory
- Have Python3 installed
- Have Docker Desktop installed and running

Once the previous have been completed, naviagte to `/src/backend/` and
run the following command:

`.\startup.ps1`

From here you should see something similar to the following output:

    Starting server...
    e62b34e857a7a9896c53d70cfd97a0b74a08903b979dff1d07bac007278af5be
    Watching for file changes with StatReloader
    Performing system checks...

    System check identified 1 issue (0 silenced).
    April 12, 2024 - 14:52:26
    Django version 5.0.3, using settings 'backend.settings'
    Starting ASGI/Daphne version 4.1.0 development server at http://127.0.0.1:8000/
    Quit the server with CTRL-BREAK.
    Server started.

From here simply naviagte to `http://127.0.0.1:8000/` to begin using the web app.

## How to Stop the Web App

Ensure you are in the `/src/backend/` directory, and run the following command:

`.\shutdown.ps1`

The web app is now shut down.

## How to Use the Web App

Here's a video demonstrating the project:

<figure class="video_container">
  <iframe src="res/learning-vids/overview-start-app.mp4/" frameborder="0" allowfullscreen="true"> 
    </iframe>
</figure>