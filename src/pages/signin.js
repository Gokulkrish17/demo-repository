import React, { useState,useEffect } from 'react'
import { FaCaretUp } from "react-icons/fa";
import { FaCaretDown } from "react-icons/fa";
import Languages from '../components/languages';
import { NavLink } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import Modal from 'react-modal';
import { Icon } from '@iconify/react/dist/iconify.js';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useDispatch,useSelector } from 'react-redux';
import { setAuth } from '../slices/authslice';
import { fetchUserProfile } from '../admindashboard/fetchuser';


const Signin = () => {
    const [user,setUser] = useState({ email: '', password:''});
    const [details,setDetails] = useState();
    const dispatch = useDispatch();
    const [otp, setOtp] = useState(Array(6).fill(''));
    const userId = useSelector((state) => state.auth.userId);
    const [signinOtp,showOtp] = useState(false);
    const [otpPage,showOtpPage] = useState(false);
    const [languages,setLanguages] = useState(false);
    const [active,setActive] = useState(false);
    const [seconds, setSeconds] = useState(300)
    const [error,setError] = useState('')
    const navigate = useNavigate()
    const handleChange = (e) => {
        const { name, value } = e.target;
          setUser({ ...user, [name]: value });
      };
      const handleShowOTP = ()=>{
        showOtp(true);
      }
      const handleCloseOTP = ()=>{
        showOtp(false);
      }
      const handleotpPage = ()=>{
        showOtpPage(true);
        showOtp(false);
      }
      const handlecloseotpPage = ()=>{
        showOtpPage(false);
      }
    const openHome = ()=>{
       navigate('/newsfeed')
    }
    const handleShowLanguages = ()=>{
        setLanguages(!languages);
    }
    const handleActive = ()=>{
        setActive(!active);
    }
    const info = [
    {
        id:1,
        heading:"Destiny ©2021"
    },
    {
        id:2,
        heading:"Sign up",
        path:"/signup"
    },
    {
        id:3,
        heading:"Sign in"
    },
    {
        id:4,
        heading:"Create ad"
    },
    {
        id:5,
        heading:"Create page"
    },
    {
        id:6,
        heading:"User Agreement"
    },
    {
        id:7,
        heading:"Privacy policy"
    },
    {
        id:8,
        heading:"Community Guidelines"
    },
    {
        id:9,
        heading:"Cookie policy"
    },
    {
        id:10,
        heading:"Copyright policy"
    },]

     const fetchUserdata = async () => {
      const token = localStorage.getItem('token');
      try {
        const response = await fetch('http://localhost:8080/api/users', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
    
        if (response.ok) {
          const data = await response.json();
          setDetails(data);
          return data;
        }
         else {
          console.error('Failed to fetch user profile:', response.status);
          return null;
        }
      } catch (error) {
        console.error('Error fetching user profile:', error);
        return null;
      }
    };
    const handleSendOtp = async (e) => {
        e.preventDefault();
        
        // Construct the payload
        const payload = {
          email: user.email
        };

        try {
          console.log('Sending payload:', JSON.stringify(payload));
          // Send the OTP request
          const response = await fetch('http://localhost:8081/api/auth/forgot-password', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
          });
          console.log('Response status:', response.status);
          console.log('Response headers:', response.headers);
      
          if (response.ok) {
            const data = await response.json();
            handleotpPage();
            toast.success('OTP sent successfully! Please check your email.');
            // Handle success based on your backend response
            // Example: If the response contains a success message
            if (!response.ok) {
            toast.error('Incorrect Username/Password');
            } else {
              setError('Incorrect Username/Password');
            }
          } else {
            // Handle response errors
            const errorText = await response.text();
            toast.error(`Error: ${errorText}`);
          }
        } catch (error) {
          console.error('Error sending OTP:', error);
          toast.error('An error occurred while sending OTP. Please try again.');
        }
      };
      
      const handleSubmit = async (e) => {
        e.preventDefault(); // Prevent default form submission
      
        // Validate user input
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      
        if (user.password.length < 8) {
          toast.error('Password must be at least 8 characters long.');
          return;
        }
        if (!user.email) {
          toast.error('Email cannot be empty.');
          return;
        } else if (!emailPattern.test(user.email)) {
          toast.error('Invalid email format.');
          return;
        }
      
        try {
          console.log(user);
          const response = await fetch('http://localhost:8081/api/auth/login', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
          });
          console.log('Response status:', response.status);
          console.log('Response headers:', response.headers);
          if (response.ok) {
            const data = await response.json();
            if (data.token) {
              localStorage.setItem('token', data.token);
              setUser({ email: '', password: '' });
              console.log('Form data submitted successfully');
              const userProfile = await fetchUserProfile(data.token);
              const userdetails = await fetchUserdata();
              if (userdetails.some(user => user.userid === userProfile.id)) {
                dispatch(setAuth({ userId: userProfile.id, token: data.token }));
                setTimeout(() => navigate('/newsfeed'), 5000);
                toast.success(`Welcome ${userProfile.name}`);
                console.log(userProfile.id);
              } else if (userProfile) {
                dispatch(setAuth({ userId: userProfile.id, token: data.token }));
                console.log('Navigating to /gw');
                toast.success(`Welcome ${userProfile.name}`);
                setTimeout(() => navigate('/gw'), 5000);
                console.log(userProfile.id);
              }
            } else {
              toast.error('Invalid credentials. Please try again.');
            }
          } else {
            const errorText = await response.text();
            toast.error(`Invalid Email/Password`);
          }
        } catch (error) {
          console.error('Error submitting form:', error);
          toast.error('An error occurred while attempting to log in. Please try again.');
        }
      };
      
  const handleOTPChange = (e, index) => {
    const { value } = e.target;
    // Ensure the value is a single digit or empty
    if (/^[0-9]?$/.test(value)) {
      const newOtp = [...otp];
      newOtp[index] = value;
      setOtp(newOtp);
      // Move focus to the next input if a value is entered
      if (value && index < otp.length - 1) {
        document.getElementById(`otp-${index + 1}`).focus();
      }
    }
  };
  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    if (otp.some(field => field === '')) {
      toast.error('Please enter the complete OTP.');
      return;
    }
    const payload = {
        email: user.email,
        otp: otp.join('')  // Assuming OTP is an array of strings
      };
    try {
      console.log('Sending data:', JSON.stringify(payload));
      const response = await fetch('http://localhost:8081/api/auth/login-with-otp', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload),
        credentials: 'include'
      });
      console.log('Response status:', response.status);
      console.log('Response headers:', response.headers);

      if (response.ok) {
        const { token } = await response.json();
        localStorage.setItem('token', token);
        console.log('Form data submitted successfully');
        handleCloseOTP()
        handlecloseotpPage()
        const userProfile = await fetchUserProfile(token);
        if (userProfile) {
          toast.success(`Welcome ${userProfile.name}`);
          dispatch(setAuth({ userId: userProfile.userId, }));
          setTimeout(() => navigate('/gw'), 5000);
        }
        else {
          toast.error('Failed to fetch user profile');
          setError('Incorrect Username/Password');
        }
      } 
      else {
        toast.error(`OTP is invalid`);
        console.error('Failed to submit data:', await response.text());
      }
    } catch (error){
      console.error('Error submitting data:', error);
      setError('Incorrect Username/Password');
    }
  };
  const handleKeyDown = (e, index) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
        // Move focus to the previous input if the current input is empty and backspace is pressed
        document.getElementById(`otp-${index - 1}`).focus();
    }
};
  useEffect(() => {
    let intervalId;
    if (otpPage && seconds > 0) {
      intervalId = setInterval(() => {
        setSeconds(prevSeconds => prevSeconds - 1);
      }, 1000);
    } else if (seconds === 0) {
      clearInterval(intervalId);
    }

    return () => clearInterval(intervalId); // Cleanup on component unmount
  }, [otpPage, seconds]);
    return (
        <>
         <div style={{fontFamily:'revert-layer'}}  className="bg-[image-url]  min-h-screen justify-center justify-between items-center bg-gradient-to-tr from-span-start to-span-end pt-20 pr-40 pl-40 flex flex-col ">
          <div  className='w-full text-font-mono flex items-center justify-between mb-40'>
            <div className='flex flex-col w-1/3'>
              <img className='w-85 h-38' src='logo.png' alt='logo' />
            </div>
            <form onSubmit={handleSubmit} className='flex flex-col bg-white px-4 rounded-md justify-center items-center md:w-2/5 py-6'>
              <div className='flex mt-6 wd-full items-center gap-5 mb-6'>
          <div className='flex gap-2'>
          <input onChange={handleChange} className='checked:bg-blue-500  form-radio' type="radio" name="option" checked={true} value="Individual" />
          <label>Individual</label>
          </div><span>|</span>
          <div className='flex gap-2'>
            <input onChange={handleChange} type="radio" name="option"  value="Identifier"/>
            <label>Identifier</label>
          </div>
          <div className='flex items-center relative'>
              <div className='flex flex-col'>
              <div className='flex items-center'>
              <p>Language</p>
              <div className=''>{active ? <FaCaretUp onClick={()=>{handleActive(); handleShowLanguages()}} /> : <FaCaretDown  onClick={()=>{handleActive(); handleShowLanguages()}}  />}</div>
              </div>
              {languages && <Languages />}
              </div>
              </div>
          </div>
          <div className='flex flex-col w-full md:w-3/4 lg:w-3/9 gap-3 mb-8'>
          <div className='flex gap-2 flex-col'>
          <label className='text-md'>Email <span className='text-red'>*</span></label>
          <input id='email' name='email' onChange={handleChange} value={user.email} className=" px-3 py-2 mt-1 text-sm border border-gray-300 rounded-md focus:outline-none focus:border-gray" type='email' required placeholder='Enter your email' />
          </div>
          <div className='flex gap-2 flex-col'>
          <label className='text-md'>Password <span className='text-red'>*</span></label>
          <input id='password' name='password' onChange={handleChange} value={user.password} className=" px-3 py-2 mt-1 text-sm border border-gray-300 rounded-md focus:outline-none focus:border-gray" type='password' required placeholder='Enter your password' />
          </div>
          <NavLink to='/forgotpassword'><p className='mb-5 text-sm hover:underline'>Forgot your password?</p></NavLink>
          <div className='cursor-pointer' onClick={handleShowOTP}>
            Login with OTP
          </div>
          <div className='flex flex-col text-center w-full gap-3' >
          <button type='submit' className="px-4 py-2 items-center border border-gray-300 bg-gradient-to-tr from-span-start w-full to-span-end text-white-800 hover:bg-custom-hover text-white font-semibold rounded-md">Log In</button>
          <p>I am a new member <NavLink to='/signup'><span className='text-highlight font-semibold hover:text-button cursor-pointer'>Sign Up Here</span></NavLink></p>
          </div>
          {error}
          </div>
          <Modal style={{}} >
          <div className='shadow-lg bg-white '>
          <span></span>
          </div>
          </Modal>
            </form>
          </div>
          <div></div>
          <Modal
  style={{
    content: {
      top: '50%',
      left: '50%',
      right: 'auto',
      bottom: 'auto',
      marginRight: '-50%',
      backgroundColor: 'transparent',
      transform: 'translate(-50%, -50%)',
      width: '40%',
      height: '45%',
      overflow: 'hidden', // Ensure content does not overflow
      border: 'none',
    },
  }}
  isOpen={signinOtp}
  onRequestClose={handleCloseOTP}
>
  <form onSubmit={handleSendOtp} className='relative bg-white shadow-lg rounded-md' style={{ width: '100%', height: '100%' }}>
    <div className="absolute flex justify-between items-center top-0 w-full h-12 cursor-pointer px-4 py-2 rounded-md border border-gray-300 bg-gradient-to-tr from-span-start to-span-end text-white-800 mb-2 text-white font-semibold">
      <p className='text-xl'>Email</p>
      <Icon className='w-6 h-6' onClick={handleCloseOTP} icon="ic:baseline-close" />
    </div>
    <div className='flex flex-col items-center justify-center h-full pt-12'>
      <div className='flex flex-col items-center gap-4'>
    <div className='rounded-full w-10 h-10 flex items-center justify-center bg-gray-200'><Icon className='w-6 h-6' icon="et:phone" /></div>
          <input id='email' name='email'
            onChange={handleChange} value={user.email}
            className='w-80 h-10 px-2 border border-gray-300'
            placeholder='Enter you email'
          />
        <div className='flex w-80 items-center justify-between'>
        <button type='submit'
          className="cursor-pointer px-4 py-2 border border-gray-300 bg-gradient-to-tr from-span-start to-span-end text-white-800 text-white font-semibold rounded-md"
        >
          Send OTP
        </button>
        </div>
      </div>
    </div>
  </form>
</Modal>
          <Modal
  style={{
    content: {
      top: '50%',
      left: '50%',
      right: 'auto',
      bottom: 'auto',
      marginRight: '-50%',
      backgroundColor: 'transparent',
      transform: 'translate(-50%, -50%)',
      width: '40%',
      height: '45%',
      overflow: 'hidden', // Ensure content does not overflow
      border: 'none',
    },
  }}
  isOpen={otpPage}
  onRequestClose={handlecloseotpPage}>
  <div className='relative bg-white shadow-lg rounded-md' style={{ width: '100%', height: '100%' }}>
    <div className="absolute flex justify-between items-center top-0 w-full h-12 cursor-pointer px-4 py-2 rounded-md border border-gray-300 bg-gradient-to-tr from-span-start to-span-end text-white-800 mb-2 text-white font-semibold">
      <p className='text-xl'>Enter your OTP</p>
      <Icon className='w-6 h-6' onClick={handlecloseotpPage} icon="ic:baseline-close" />
    </div>
    <div className='flex flex-col items-center justify-center h-full pt-12'>
      <div className='flex flex-col items-center gap-4'>
    <div className='rounded-full w-10 h-10 flex items-center justify-center bg-gray-200'><Icon icon="material-symbols:mail-outline" /></div>
        <div>
          <input name='email' id='email'
            value={user.email}
            className='w-80 h-10 px-2 border border-gray-300'
            readOnly
          />
          <p className='text-sm text-gray-500'>OTP has been sent to {user.email}</p>
        </div>
        <div className='flex gap-2'>
          {otp.map((value, index) => (
            <input
              key={index}
              id={`otp-${index}`}
              className='px-3 py-2 text-sm border w-12 border-gray rounded-md focus:border-gray text-center'
              type='text'
              maxLength='1'
              onChange={(e) => handleOTPChange(e, index)}
              onKeyDown={(e) => handleKeyDown(e, index)}
              value={value}
              placeholder='0'
            />
          ))}
        </div>
        <div className='flex w-80 items-center justify-between'>
        <button className='px-4 py-2 bg-gray-100 shadow-md rounded-md'>Resend OTP</button>
        <button
          className="cursor-pointer px-4 py-2 border border-gray-300 bg-gradient-to-tr from-span-start to-span-end text-white-800 text-white font-semibold rounded-md"
          onClick={handleVerifyOtp}
          type='submit'
        >
          Submit
        </button>
        </div>
        <span><span> {seconds > 0 ? `Time left: ${Math.floor(seconds / 60)}:${String(seconds % 60).padStart(2, '0')}` : 'Time’s up!'}</span>
        </span>
      </div>
    </div>
  </div>
</Modal>
          <div className="flex flex-col w-full sm:items-center py-4 sm:gap-2 md:flex-row md:justify-center md:gap-3.5">
  {info.map((detail, index) => (
    <React.Fragment key={index}>
      <NavLink to={detail.path}><p className="cursor-pointer hover:text-white">{detail.heading}</p></NavLink>
      {index !== info.length - 1 && <span className="hidden md:inline-block">|</span>}
    </React.Fragment>
  ))}
</div>
<ToastContainer />
         </div>
        </>
      );
}

export default Signin;
