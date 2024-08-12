import React,{useState} from 'react'
import Weather from '../components/weather'
import Createpost from '../components/createpost'
import Suggestfriends from '../components/suggestfriends'
import Events from '../components/events'
import Reels from '../components/reels'
import Invite from '../components/invite'
import Birthday from '../components/birthday'
import Blogs from '../components/blogs'
import Jobposting from '../components/job posting'
import Advertisement from '../components/ad'
import Story from '../components/story'
import Createjob from '../components/addjob'
import { Icon } from '@iconify/react/dist/iconify.js'
import {Tooltip } from 'react-tooltip'
import { Link, useParams } from 'react-router-dom'
import Post from '../components/poste'
import { useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'

const Home = () => {
  const[shortcut,showShortcut] = useState(false);
  const navigate = useNavigate();
  const {Friends} = useSelector((state)=>state.friend)
  const handleShortcut =()=>{
    showShortcut(!shortcut);
  }
  const openFriendSuggestion = ()=>{
    navigate('/addfriends')
  }
  const options = [{
    id:1,
    name:"Profile",
    icon:<Icon className='optionicon' icon="gravity-ui:person" />,
    path:'/profile'
  },
  {
    id:2,
    name:"Chat",
    icon:<Icon className='optionicon' icon="mingcute:message-4-line" />,
    path:'/messages'
  },
  {
    id:3,
    name:"My Pages",
    icon:<Icon className='optionicon' icon="icon-park-solid:web-page" />,
    path:'/page'
  },
  {
    id:4,
    name:"Friends",
    icon:<Icon className='optionicon' icon="icon-park-outline:peoples-two" />,
    path:'/friends'
  },
  {
    id:5,
    name:"Photos",
    icon:<Icon className='optionicon' icon="ph:image-duotone" />,
    path:'/photos'
  },
  {
    id:6,
    name:"Videos",
    icon:<Icon className='optionicon' icon="bxs:videos" />,
    path:'/videos'
  },{
    id:7,
    name:"Notifications",
    icon:<Icon className='optionicon' icon="mi:notification" />,
    path:'/notifications'
  },
  {
    id:8,
    name:"Saved",
    icon:<Icon icon="foundation:book-bookmark" />,
    path:'/saved'
  },
  {
    id:9,
    name:"Logout",
    icon:<Icon className='optionicon' icon="fe:logout" />,
  }]
  
  return(
    <div className='w-full bg-gray-50  px-20 py-4 flex flex-col gap-4'>
      <div className='fixed flex flex-col gap-4 left-0 h-auto'>
      <div data-tooltip-content='Shortcut' data-tooltip-id='mytooltip' onClick={handleShortcut} className='w-11 h-11 flex items-center cursor-pointer justify-center border bg-cta rounded-full '>
      <Icon className='w-6 h-6 text-white' icon="mynaui:menu" />
      </div>
      {shortcut &&(
      <div className=' flex flex-col gap-4 left-0 h-auto'>
        {options.map((option)=>(
      <div key={option.id} className='w-11 h-11 slide-in-down duration-500 flex items-center cursor-pointer justify-center border bg-cta rounded-full'>
      <Link to={option.path}><span data-tooltip-content= {option.name} data-tooltip-id= "mytooltip" className='flex items-center justify-center text-white'>{option.icon}</span></Link>
      </div>
        ))}
      </div>
      )}
      </div>
    <div className='w-full justify-between flex flex-wrap gap-4 '>
    <aside className='md:w-1/5 sm:w-full xs:w-full flex flex-col gap-4'>
    <Weather />
    <Birthday />
    <Events />
    <Advertisement />
    </aside> 
    <div className='md:w-1/2 flex flex-col gap-4 sm:w-full'>
    {Friends.length>0&&<Reels />} 
    <Createpost />
    {Friends.length>0&&<Post />} 
    {Friends.length===0&&  
    <div className="flex bg-gradient-to-tr from-span-start to-span-end text-white flex-col justify-center gap-4 h-96 items-center">
      <div className='flex flex-col items-center'>
         <div className="border-2 p-4 rounded-full text-center w-max"><Icon className="w-11 h-11" icon="fa-solid:user-friends" /></div>
         <p className="text-xl font-semibold">Add Friends</p>
      </div>
         <span onClick={openFriendSuggestion} className='border border-white p-2 hover:bg-white hover:text-cta rounded-md cursor-pointer font-semibold'>View Suggestions</span>
         </div>}
    </div>
    <aside className='flex flex-col gap-4 md:w-1/4 sm:w-full'>
    <Suggestfriends />
    <Story />
    <Jobposting />
    <Createjob />
    <Blogs />
    <Invite />
    </aside>
    </div>
    <Tooltip id="mytooltip" />
    </div>
  )
}
export default Home
