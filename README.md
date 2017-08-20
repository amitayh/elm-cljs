# elm-cljs

Attempting to implement [The Elm Architecture](https://guide.elm-lang.org/architecture/)™ in ClojureScript

[Demo here](https://amitayh.github.io/elm-cljs/)

## Overview

[app.cljs](src/elm_cljs/app.cljs) contains an example app, written in a purely-functional manner.
All side effects (for example, generating random values or fetching data from a remote endpoint)
are handled by dispatching effects which run outside your program, and report back using messages
on `core.async` channels

## How does it work?

The basic pattern is identical to what you'll find in [Elm's docs](https://guide.elm-lang.org/) -
your app consists of 3 basic parts:

 * **Model** - the state of your application
 * **Update** - a way to update your state
 * **View** - a way to view your state as HTML

The example app in [app.cljs](src/elm_cljs/app.cljs) contains the same parts - the model is
a simple map representing your app's state (a counter and some text to display).

Updates are being applied using messages, each representing some action to perform. These
messages can come wither from user interactions (such as button clicks and other inputs),
or from the outside world (like HTTP calls or web sockets)

The update function is a multimethod dispatching on the type of the message. It receives the
current model and the message - and should return a tuple of `[next-model effect]`:

 * `next-model` - the next state of your app after the message has been applied
 * `effect` - if there's some side effect to be performed from this action - for example,
 making an HTTP call, requesting a random value etc. If we want our app to be purely functional
 all effects should be handled like this - making our app a few immutable data structures and
 pure functions. If no effect is needed for our action, we can return `nil`

Note that every event handler in our view if just a function returning some message. We do not
modify the app state directly.

Both effects and messages are being conveyed on [core.async](https://github.com/clojure/core.async) channels.

### Main

The `main` function in [core.cljs](src/elm_cljs/core.cljs) is responsible for wiring everything up.
It runs a `go-loop`, which fetches the next message from the `messages` channel. Then, it applies
the message using the `update` function, renders the view and waits for the next message. It's also
responsible for sending effects to the `effects` channel if needed.

### Render

The `render` function in [render.cljs](src/elm_cljs/render.cljs) is responsible for transforming
the hiccup-style views into [React](https://facebook.github.io/react/) elements. The transformation
includes some magic of wrapping every event handler (element props which start with `on*`) with a
function that will send the message to the `messages` channel.

### Effects

Effects are designed to be extensible by defining an `Effect` protocol in [effects.cljs](src/elm_cljs/effects.cljs).
You can see example of random number generation and fake HTTP call. It leverages `core.async` to handle
the async flow. This file also includes the code that continuously reads the next effect to perform from
the `effects` channel and runs it.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2017 Amitay Horwitz

Distributed under the MIT License
