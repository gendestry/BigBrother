import { createBrowserRouter } from "react-router-dom";
import App from "./views/App.tsx";
import Minecraft from "./views/Minecraft.tsx";
import UserList from "./views/admin/UserList.tsx";

export const router = createBrowserRouter([
    {
        path: "/",
        element: (<App />),
        children: [
            {
                path: "/minecraft",
                element: <Minecraft />
            },
            {
                path: "/contact",
                element: <div>Contact</div>
            },
            {
                path: "/github",
                element: <div>GitHub</div>
            },
            {
                path: "/listusers",
                element: <UserList />
            }
        ]
    }
]);